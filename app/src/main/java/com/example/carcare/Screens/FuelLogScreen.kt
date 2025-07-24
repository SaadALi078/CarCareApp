@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.carcare.Screens // Corrected: Lowercase package name

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carcare.viewmodels.FuelLog
import com.example.carcare.viewmodels.VehicleDashboardViewModel

// Note: This file should be located at: .../app/src/main/java/com/example/carcare/screens/FuelLogScreen.kt

@Composable
fun FuelLogScreen(navController: NavController, vehicleId: String) {
    val viewModel: VehicleDashboardViewModel = viewModel()

    // Load fuel logs when the screen appears
    LaunchedEffect(vehicleId) {
        viewModel.loadFuelLogs(vehicleId)
    }

    // Use a derived state to recalculate only when logs change
    val efficiencyData by remember(viewModel.fuelLogs) {
        derivedStateOf {
            calculateEfficiencySummary(viewModel.fuelLogs)
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
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            if (efficiencyData.totalDistance > 0) {
                EfficiencySummaryCard(efficiencyData)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (viewModel.fuelLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No fuel logs yet. Add your first one!")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.fuelLogs, key = { it.id }) { log ->
                        FuelLogCard(
                            log = log,
                            onDelete = { viewModel.deleteFuelLog(vehicleId, log.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FuelLogCard(log: FuelLog, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(log.date, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete log")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoColumn("Amount", "${log.amount} L")
                InfoColumn("Cost", "${log.cost} PKR")
                InfoColumn("Odometer", "${log.odometer} km")
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Notes: ${log.notes}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun RowScope.InfoColumn(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

data class EfficiencyData(
    val avgEfficiency: Double,
    val avgCostPerKm: Double,
    val totalDistance: Int,
    val logCount: Int
)

private fun calculateEfficiencySummary(logs: List<FuelLog>): EfficiencyData {
    if (logs.size < 2) return EfficiencyData(0.0, 0.0, 0, logs.size)

    val sortedLogs = logs.sortedBy { it.odometer }
    var totalDistance = 0
    var totalFuel = 0.0
    var totalCost = 0.0

    for (i in 1 until sortedLogs.size) {
        val distance = sortedLogs[i].odometer - sortedLogs[i - 1].odometer
        if (distance > 0) {
            val fuel = sortedLogs[i].amount
            totalDistance += distance
            totalFuel += fuel
            totalCost += sortedLogs[i].cost
        }
    }

    val avgEfficiency = if (totalFuel > 0) totalDistance / totalFuel else 0.0
    val avgCostPerKm = if (totalDistance > 0) totalCost / totalDistance else 0.0

    return EfficiencyData(avgEfficiency, avgCostPerKm, totalDistance, logs.size)
}

@Composable
fun EfficiencySummaryCard(data: EfficiencyData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Fuel Efficiency Summary", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoColumn("Avg Efficiency", "${"%.2f".format(data.avgEfficiency)} km/L")
                InfoColumn("Total Distance", "${data.totalDistance} km")
                InfoColumn("Avg Cost/km", "${"%.2f".format(data.avgCostPerKm)} PKR")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Based on ${data.logCount} logs over ${data.totalDistance} km", style = MaterialTheme.typography.bodySmall)
        }
    }
}