@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.carcare.navigation.Screen

data class FuelLog(
    val id: String = "",
    val amount: String = "",
    val cost: String = "",
    val date: String = "",
    val odometer: String = "",
    val notes: String = ""
)

@Composable
fun FuelLogScreen(navController: NavController, vehicleId: String) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var fuelLogs by remember { mutableStateOf(listOf<FuelLog>()) }
    var isLoading by remember { mutableStateOf(true) }
    var listener: ListenerRegistration? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        listener = db.collection("users")
            .document(uid)
            .collection("vehicles")
            .document(vehicleId)
            .collection("fuel_logs")
            .addSnapshotListener { snapshot, error ->
                isLoading = false
                if (error != null) {
                    Toast.makeText(context, "Failed to load logs", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                fuelLogs = snapshot?.documents?.mapNotNull { doc ->
                    FuelLog(
                        id = doc.id,
                        amount = doc.getString("amount") ?: "",
                        cost = doc.getString("cost") ?: "",
                        date = doc.getString("date") ?: "",
                        odometer = doc.getString("odometer") ?: "",
                        notes = doc.getString("notes") ?: ""
                    )
                } ?: emptyList()
            }

        onDispose {
            listener?.remove()
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
                Icon(Icons.Default.Add, contentDescription = "Add Fuel Log")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (fuelLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No fuel logs found.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(fuelLogs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Date: ${log.date}", style = MaterialTheme.typography.titleSmall)
                                Text("Amount: ${log.amount} L")
                                Text("Cost: PKR ${log.cost}")
                                Text("Odometer: ${log.odometer} km")
                                if (log.notes.isNotBlank()) {
                                    Text("Notes: ${log.notes}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
