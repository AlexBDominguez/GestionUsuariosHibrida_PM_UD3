package com.example.pm_ud3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pm_ud3.data.RepositoryResult
import com.example.pm_ud3.data.TestUsers
import com.example.pm_ud3.data.UserRepository
import com.example.pm_ud3.data.local.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel (
    private val repository: UserRepository
) : ViewModel(){

    //Estado de la UI
    val users = repository.getAllUsersStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    //CRUD

    fun insertUser(user: User){
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun updateUser(user: User){
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User){
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }


    //Usuario de prueba
    fun addTestUser(){
        viewModelScope.launch {
            val testUser = TestUsers.users.random()
            val currentUsers = users.value

            // Verificar si ya existe un usuario con el mismo email, nombre y apellido para evitar duplicados
            val userExists = currentUsers.any {
                it.email == testUser.email ||
                (it.firstName == testUser.firstName && it.lastName == testUser.lastName)
            }

            if (!userExists) {
                val user = testUser.copy(
                    id = "local_${System.nanoTime()}",
                    pendingSync = true,
                    pendingDelete = false
                )
                repository.insertUser(user)
            }
        }
    }

    //Sincronización
    fun sync(){
        viewModelScope.launch {
            val uploadResult = repository.uploadPendingChanges()
            if(uploadResult is RepositoryResult.Success){
                repository.syncFromServer()
            }
        }
    }
}