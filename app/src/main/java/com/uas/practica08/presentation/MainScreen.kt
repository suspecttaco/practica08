package com.uas.practica08.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uas.practica08.data.User
import com.uas.practica08.domain.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateMainScreen() {
    val context = LocalContext.current
    // Obtener db
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.userDao()
    // Variable para corrutinas
    val coroutineScope = rememberCoroutineScope()
    // collectAsState() transforma el flow en un estado que redibuja la UI cuando hay cambios
    val users by dao.getAll().collectAsState(initial = emptyList())

    var newName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp)
    ) {
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Nombre del usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newName.isNotBlank()) {
                    coroutineScope.launch(Dispatchers.IO) {
                        // Guardar en db (hilo secundario)
                        dao.insert(User(name = newName))

                        // Regresar al hilo principal para actualizar UI
                        withContext(Dispatchers.Main) {
                            newName = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Usuario")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Lista de Usuarios Registrados:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(users) {
                    user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ID: ${user.id} | Nombre: ${user.name}",
                            modifier = Modifier.padding(16.dp)
                        )

                        // Entregable: Boton DELETE
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    dao.delete(user)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar usuario"
                            )
                        }
                    }

                }
            }
        }
    }
}