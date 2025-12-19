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

    @Query("SELECT id FROM users")
    suspend fun getUsersIds():List<String>

    @Query("SELECT * FROM users WHERE pendingSync= 1")
    suspend fun getPendingUpdates(): List<User>

    @Query("SELECT * FROM users WHERE pendingDelete = 1")
    suspend fun getPendingDeletes():List<User>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE firstName = :firstName AND lastName = :lastName LIMIT 1")
    suspend fun getUserByName(firstName: String, lastName: String): User?

    // Escritura

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Update
    suspend fun updateUsers(users:List<User>)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

}