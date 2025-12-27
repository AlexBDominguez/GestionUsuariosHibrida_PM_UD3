package com.example.pm_ud3.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Lectura
    @Query("SELECT * FROM users")
    fun getAllUsersStream(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE pendingSync= 1")
    suspend fun getPendingUpdates(): List<User>

    @Query("SELECT * FROM users WHERE pendingDelete = 1")
    suspend fun getPendingDeletes(): List<User>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // Escritura
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

