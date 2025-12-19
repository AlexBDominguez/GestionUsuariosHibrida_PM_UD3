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
            // Verificar si ya existe un usuario con el mismo email o nombre completo
            val existingByEmail = local.getUserByEmail(user.email)
            val existingByName = local.getUserByName(user.firstName, user.lastName)

            if (existingByEmail != null || existingByName != null) {
                // El usuario ya existe, no insertar duplicado
                // Se podría añadir un log aquí si fuera necesario: Log.d("UserRepo", "Usuario duplicado evitado")
                return RepositoryResult.Success()
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
                local.deleteUser(user)  // Ahora usa el método @Delete correctamente
            }

            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }

    override suspend fun syncFromServer(): RepositoryResult<Unit> {
        return try {
            val remoteUsers = remote.getAllUsers()

            // Para cada usuario remoto, verificar si ya existe uno local con el mismo email
            for (remoteUser in remoteUsers) {
                val existingLocalUser = local.getUserByEmail(remoteUser.email)

                val userToSync = remoteUser.toLocalUser().copy(
                    pendingSync = false,
                    pendingDelete = false
                )

                if (existingLocalUser != null) {
                    // Si existe un usuario local con el mismo email, actualizar en lugar de insertar
                    if (existingLocalUser.id.startsWith("local_")) {
                        // El usuario local necesita actualizarse con el ID del servidor
                        local.deleteUser(existingLocalUser)
                        local.insertUser(userToSync)
                    } else if (existingLocalUser.id != remoteUser.id) {
                        // IDs diferentes pero mismo email - priorizar el del servidor
                        local.deleteUserById(existingLocalUser.id)
                        local.insertUser(userToSync)
                    } else {
                        // Mismo ID, solo actualizar los datos
                        local.insertUser(userToSync)
                    }
                } else {
                    // Usuario completamente nuevo, insertar directamente
                    local.insertUser(userToSync)
                }
            }

            RepositoryResult.Success()
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }
}


