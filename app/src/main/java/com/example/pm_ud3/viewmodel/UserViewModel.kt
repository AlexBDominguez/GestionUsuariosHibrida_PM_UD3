package com.example.pm_ud3.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pm_ud3.data.RepositoryResult
import com.example.pm_ud3.data.TestUsers
import com.example.pm_ud3.data.UserRepository
import com.example.pm_ud3.data.local.User
import com.example.pm_ud3.sensors.ShakeDetector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel (
    private val repository: UserRepository
) : ViewModel(){

    // Sistema de eventos para snackbars
    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events.asSharedFlow()

    // Detector de shake
    private var shakeDetector: ShakeDetector? = null

    //Estado de la UI
    val users = repository.getAllUsersStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /**
     * Configura el shake listener para añadir usuarios aleatorios
     */
    fun setupShakeListener(context: Context) {
        if (shakeDetector == null) {
            shakeDetector = ShakeDetector(context) {
                addTestUser()
            }
            shakeDetector?.start()
        }
    }

    override fun onCleared() {
        super.onCleared()
        shakeDetector?.stop()
    }

    private suspend fun emitEvent(message: String) {
        _events.emit(message)
    }

    //CRUD

    fun insertUser(user: User){
        viewModelScope.launch {
            try {
                val result = repository.insertUser(user)
                when (result) {
                    is RepositoryResult.Success -> {
                        emitEvent("Usuario creado correctamente")
                    }
                    is RepositoryResult.Error -> {
                        emitEvent("Error: ${result.exception.message}")
                    }
                }
            } catch (e: Exception) {
                emitEvent("Error inesperado: ${e.message}")
            }
        }
    }

    fun updateUser(user: User){
        viewModelScope.launch {
            repository.updateUser(user)
            emitEvent("Usuario actualizado correctamente")
        }
    }

    fun deleteUser(user: User){
        viewModelScope.launch {
            repository.deleteUser(user)
            emitEvent("Usuario eliminado correctamente")
        }
    }


    //Usuario de prueba
    fun addTestUser(){
        viewModelScope.launch {
            try {
                val testUser = TestUsers.users.random()
                val currentUsers = users.value

                // Verificar duplicados por email y nombre completo (excluyendo usuarios eliminados)
                val userExists = currentUsers.any { user ->
                    !user.pendingDelete && (
                        user.email == testUser.email ||
                        (user.firstName == testUser.firstName && user.lastName == testUser.lastName)
                    )
                }

                if (userExists) {
                    emitEvent("El usuario ${testUser.firstName} ${testUser.lastName} ya existe")
                    return@launch
                }

                val user = testUser.copy(
                    id = "local_${System.nanoTime()}",
                    pendingSync = true,
                    pendingDelete = false
                )

                val result = repository.insertUser(user)
                when (result) {
                    is RepositoryResult.Success -> {
                        emitEvent("Usuario añadido: ${user.firstName} ${user.lastName}")
                    }
                    is RepositoryResult.Error -> {
                        emitEvent("Error: ${result.exception.message}")
                    }
                }
            } catch (e: Exception) {
                emitEvent("Error al añadir usuario: ${e.message}")
            }
        }
    }

    //Sincronización
    fun sync(){
        viewModelScope.launch {
            try {
                val uploadResult = repository.uploadPendingChanges()
                val syncResult = repository.syncFromServer()

                when {
                    uploadResult is RepositoryResult.Success && syncResult is RepositoryResult.Success -> {
                        emitEvent("Sincronización completada")
                    }
                    syncResult is RepositoryResult.Success -> {
                        emitEvent("Datos descargados (sin cambios locales)")
                    }
                    else -> {
                        emitEvent("Error de conexión - trabajando offline")
                    }
                }
            } catch (e: Exception) {
                emitEvent("Error de sincronización: Sin conexión")
            }
        }
    }

    // Funciones para testing/pruebas del PDF
    fun clearDatabase() {
        viewModelScope.launch {
            try {
                val result = repository.clearLocalDatabase()
                when (result) {
                    is RepositoryResult.Success -> {
                        emitEvent("Base de datos limpia - Lista para pruebas")
                    }
                    is RepositoryResult.Error -> {
                        emitEvent("Error al limpiar BD: ${result.exception.message}")
                    }
                }
            } catch (e: Exception) {
                emitEvent("Error al limpiar base de datos: ${e.message}")
            }
        }
    }
}