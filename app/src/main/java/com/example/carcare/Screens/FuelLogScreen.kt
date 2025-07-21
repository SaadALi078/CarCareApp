@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.carcare.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.carcare.navigation.Screen

@Composable
fun FuelLogScreen(navController: NavController, vehicleId: String) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()

    var logs by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load fuel logs
    LaunchedEffect(true) {
        db.collection("users")
            .document(uid)
            .collection("vehicles")
            .document(vehicleId)
            .collection("fuel_logs")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Failed to load fuel logs", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    logs = snapshot.documents.map { it.data?.plus("id" to it.id) ?: emptyMap() }
                    isLoading = false
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fuel Logs") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add_fuel/$vehicleId")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Fuel")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (logs.isEmpty()) {
                Text("No fuel logs yet.")
            } else {
                logs.forEach { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Date: ${log["date"]}")
                            Text("Amount: ${log["amount"]} L")
                            Text("Cost: ${log["cost"]} PKR")
                            Text("Odometer: ${log["odometer"]} km")
                            if (log["notes"].toString().isNotBlank()) {
                                Text("Notes: ${log["notes"]}")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                IconButton(onClick = {
                                    val logId = log["id"] as? String ?: return@IconButton
                                    db.collection("users")
                                        .document(uid)
                                        .collection("vehicles")
                                        .document(vehicleId)
                                        .collection("fuel_logs")
                                        .document(logId)
                                        .delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Log deleted", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                                        }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
