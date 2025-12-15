package com.example.pm_ud3.data

import com.example.pm_ud3.data.local.User
import com.example.pm_ud3.data.local.UserDao
import com.example.pm_ud3.network.MockApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class DefaultUserRepository(
    private val local: UserDao,
    private val remote: MockApiService
) : UserRepository {

    // ---------- LECTURA ----------

    override fun getAllUsersStream(): Flow<List<User>> {
        return local.getAllUsersStream()
            .map { users ->
                users.filter { !it.pendingDelete }
            }
    }

    // ---------- CRUD LOCAL ----------

    override suspend fun insertUser(user: User): RepositoryResult<Unit> {
        return try {
            local.insertUser(user.copy(pendingSync = true))
            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }

    override suspend fun updateUser(user: User): RepositoryResult<Unit> {
        return try {
            local.updateUser(user.copy(pendingSync = true))
            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }

    override suspend fun deleteUser(user: User): RepositoryResult<Unit> {
        return try {
            local.updateUser(
                user.copy(
                    pendingDelete = true,
                    pendingSync = true
                )
            )
            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }

    // ---------- SINCRONIZACIÓN ----------

    override suspend fun uploadPendingChanges(): RepositoryResult<Unit> {
        return try {
            // ALTAS / MODIFICACIONES
            val pendingUpdates = local.getPendingUpdates()

            for (user in pendingUpdates) {
                if (user.pendingDelete) continue

                val remoteUser = user.toRemoteUser()

                if (user.id.startsWith("local_")) {
                    // CREATE
                    val created = remote.createUser(remoteUser)
                    local.deleteUser(user)
                    local.insertUser(created.toLocalUser())
                } else {
                    // UPDATE
                    remote.updateUser(user.id, remoteUser)
                    local.updateUser(user.copy(pendingSync = false))
                }
            }

            // BORRADOS
            val pendingDeletes = local.getPendingDeletes()

            for (user in pendingDeletes) {
                if (!user.id.startsWith("local_")) {
                    remote.deleteUser(user.id)
                }
                local.deleteUser(user)
            }

            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }

    override suspend fun syncFromServer(): RepositoryResult<Unit> {
        return try {
            val remoteUsers = remote.getAllUsers()
            val localIds = local.getUsersIds().toSet()

            val usersToInsert = mutableListOf<User>()
            val usersToUpdate = mutableListOf<User>()

            for (remoteUser in remoteUsers) {
                val localUser = remoteUser.toLocalUser()

                if (localIds.contains(remoteUser.id)) {
                    usersToUpdate.add(localUser)
                } else {
                    usersToInsert.add(localUser)
                }
            }

            if (usersToUpdate.isNotEmpty()) {
                local.updateUsers(usersToUpdate)
            }

            if (usersToInsert.isNotEmpty()) {
                local.insertUsers(usersToInsert)
            }

            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }
}


