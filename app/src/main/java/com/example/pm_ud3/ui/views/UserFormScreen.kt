package com.example.pm_ud3.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pm_ud3.data.local.User
import com.example.pm_ud3.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    viewModel: UserViewModel,
    userId: String?,
    onDone: () -> Unit
) {
    val users by viewModel.users.collectAsState()

    val existingUser = users.find { it.id == userId }
    val isEditing = existingUser != null

    var firstName by remember { mutableStateOf(existingUser?.firstName ?: "") }
    var lastName by remember { mutableStateOf(existingUser?.lastName ?: "") }
    var email by remember { mutableStateOf(existingUser?.email ?: "") }
    var age by remember { mutableStateOf(existingUser?.age?.toString() ?: "") }
    var userName by remember { mutableStateOf(existingUser?.userName ?: "") }
    var positionTitle by remember { mutableStateOf(existingUser?.positionTitle ?: "") }
    var imagen by remember { mutableStateOf(existingUser?.imagen ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Usuario" else "Nuevo Usuario") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {


            TextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Nombre") })
            TextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") })
            TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            TextField(value = age, onValueChange = { age = it }, label = { Text("Edad") })
            TextField(value = userName, onValueChange = { userName = it }, label = { Text("Username") })
            TextField(value = positionTitle, onValueChange = { positionTitle = it }, label = { Text("Puesto") })
            TextField(value = imagen, onValueChange = { imagen = it }, label = { Text("Imagen URL") })

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val user = User(
                            id = existingUser?.id ?: "local_${System.nanoTime()}",
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            age = age.toIntOrNull() ?: 0,
                            userName = userName,
                            positionTitle = positionTitle,
                            imagen = imagen,
                            pendingSync = true
                        )

                        if (existingUser == null) {
                            viewModel.insertUser(user)
                        } else {
                            viewModel.updateUser(user)
                        }

                        onDone()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isEditing) "Actualizar" else "Guardar")
                }
            }
        }
    }
}