package com.example.pm_ud3.data

import com.example.pm_ud3.data.local.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAllUsersStream(): Flow<List<User>>

    suspend fun insertUser(user: User): RepositoryResult<Unit>

    suspend fun updateUser(user: User): RepositoryResult<Unit>

    suspend fun deleteUser(user: User): RepositoryResult<Unit>

    suspend fun uploadPendingChanges(): RepositoryResult<Unit>

    suspend fun syncFromServer(): RepositoryResult<Unit>

    // Funciones para testing
    suspend fun clearLocalDatabase(): RepositoryResult<Unit>
}
