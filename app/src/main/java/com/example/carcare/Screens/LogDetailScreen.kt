@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.example.carcare.data.model.MaintenanceLog

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LogDetailScreen(navController: NavController, vehicleId: String, logId: String) {
    val context = LocalContext.current
    var log by remember { mutableStateOf<MaintenanceLog?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load log
    LaunchedEffect(logId, vehicleId) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("vehicles")
            .document(vehicleId)
            .collection("maintenance")
            .document(logId)
            .get()
            .addOnSuccessListener { doc ->
                doc?.let {
                    log = MaintenanceLog(
                        id = doc.id,
                        type = doc.getString("type") ?: "",
                        date = doc.getString("date") ?: "",
                        mileage = (doc.getLong("mileage") ?: 0).toInt(),
                        notes = doc.getString("notes") ?: "",
                        reminderType = doc.getString("reminderType") ?: "",
                        reminderValue = doc.getString("reminderValue") ?: "",
                        vehicleId = vehicleId
                    )
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Details") },
                actions = {
                    IconButton(onClick = {
                        log?.let {
                            navController.navigate(Screen.AddMaintenance.route + "?editId=${it.id}&vehicleId=${it.vehicleId}")
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        log?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Type: ${it.type}", style = MaterialTheme.typography.titleLarge)
                Text("Date: ${it.date}")
                Text("Mileage: ${it.mileage}")
                if (it.notes.isNotBlank()) Text("Notes: ${it.notes}")
                if (it.reminderType.isNotBlank()) {
                    Text("Reminder: ${it.reminderType} - ${it.reminderValue}")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete", color = Color.White)
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        if (showDeleteDialog && log != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Maintenance?") },
                text = { Text("Are you sure you want to delete this record?") },
                confirmButton = {
                    TextButton(onClick = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@TextButton
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .collection("vehicles")
                            .document(vehicleId)
                            .collection("maintenance")
                            .document(logId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        showDeleteDialog = false
                    }) {
                        Text("Yes", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
