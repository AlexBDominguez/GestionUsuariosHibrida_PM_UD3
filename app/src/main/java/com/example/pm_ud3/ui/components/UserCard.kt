package com.example.pm_ud3.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pm_ud3.data.local.User


@Composable
fun UserCard(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(text = user.email)
            Text(text = "Edad: ${user.age}")
            Text(text = user.positionTitle)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(onClick = onEdit) {
                    Text("Editar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}