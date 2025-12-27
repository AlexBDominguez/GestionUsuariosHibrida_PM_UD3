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
            // Verificar duplicados solo por email, excluyendo usuarios marcados para eliminar
            val existingByEmail = local.getUserByEmail(user.email)

            if (existingByEmail != null && !existingByEmail.pendingDelete) {
                return RepositoryResult.Error(Exception("Ya existe un usuario con este email"))
            }

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
                    val newUser = created.toLocalUser().copy(
                        pendingSync = false,
                        pendingDelete = false
                    )
                    // Primero insertar el nuevo usuario, luego eliminar el local
                    local.insertUser(newUser)
                    local.deleteUser(user)
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

            // Insertar o actualizar cada usuario remoto
            for (remoteUser in remoteUsers) {
                val existingLocalUser = local.getUserByEmail(remoteUser.email)
                val userToSync = remoteUser.toLocalUser().copy(
                    pendingSync = false,
                    pendingDelete = false
                )

                if (existingLocalUser != null) {
                    // Usuario existe - actualizar datos
                    val updatedUser = userToSync.copy(id = existingLocalUser.id)
                    local.updateUser(updatedUser)
                } else {
                    // Usuario nuevo - insertar directamente (sin verificaciones adicionales)
                    local.insertUser(userToSync)
                }
            }

            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }

    // Funciones para testing
    override suspend fun clearLocalDatabase(): RepositoryResult<Unit> {
        return try {
            local.deleteAllUsers()
            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }
}


