package com.example.pm_ud3.data.local

import androidx.room.Dao
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

    // Escritura

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Update
    suspend fun updateUsers(users:List<User>)

    @Update
    suspend fun deleteUser(user:User)

}