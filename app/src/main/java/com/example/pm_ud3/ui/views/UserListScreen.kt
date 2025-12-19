package com.example.pm_ud3.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.pm_ud3.viewmodel.UserViewModel
import com.example.pm_ud3.ui.components.UserCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserViewModel,
    onAddUser: () -> Unit,
    onEditUser: (String) -> Unit
) {
    val users by viewModel.users.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios") },
                actions = {
                    IconButton(onClick = { viewModel.sync() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sincronizar con servidor")
                    }

                    IconButton(onClick = { viewModel.addTestUser() }) {
                        Icon(Icons.Default.Person, contentDescription = "Añadir usuario de prueba")
                    }

                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddUser) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(users) { user ->
                UserCard(
                    user = user,
                    onEdit = { onEditUser(user.id) },
                    onDelete = { viewModel.deleteUser(user) }
                )
            }
        }
    }
}