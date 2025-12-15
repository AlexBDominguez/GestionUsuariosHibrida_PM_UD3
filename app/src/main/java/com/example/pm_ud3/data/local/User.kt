package com.example.pm_ud3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val userName: String,
    val positionTitle: String,
    val imagen: String,
    val pendingSync: Boolean = false,
    val pendingDelete: Boolean = false
)
