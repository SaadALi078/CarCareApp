@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carcare.R
import com.example.carcare.data.model.MaintenanceLog
import com.example.carcare.viewmodels.MaintenanceViewModel

@Composable
fun MaintenanceScreen(navController: NavController, vehicleId: String) {
    val viewModel: MaintenanceViewModel = viewModel()
    val logs by viewModel.logs.collectAsState()

    LaunchedEffect(vehicleId) {
        viewModel.loadLogs(vehicleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Logs") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_maintenance?vehicleId=$vehicleId")
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Maintenance")
            }
        }
    ) { paddingValues ->
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No maintenance records found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(logs, key = { it.id }) { log ->
                    MaintenanceCard(
                        log = log,
                        onClick = {
                            navController.navigate("add_maintenance?vehicleId=$vehicleId&editId=${log.id}")
                        },
                        onDelete = {
                            viewModel.deleteLog(vehicleId, log.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MaintenanceCard(log: MaintenanceLog, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(getIconForType(log.type)),
                    contentDescription = log.type,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(log.type, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))

                // ✏️ Edit Icon
                IconButton(onClick = onClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }

                // 🗑️ Delete Icon
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Mileage: ${log.mileage}")

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Notes: ${log.notes}", style = MaterialTheme.typography.bodySmall)
            }

            Text(log.date, style = MaterialTheme.typography.labelSmall)
        }
    }
}

fun getIconForType(type: String): Int {
    return when (type.lowercase()) {
        "oil change" -> R.drawable.ic_oil
        "tire rotation" -> R.drawable.ic_tire
        "brake check" -> R.drawable.ic_brake
        "fluid top-up" -> R.drawable.ic_fluid
        else -> R.drawable.ic_maintenance
    }
}
