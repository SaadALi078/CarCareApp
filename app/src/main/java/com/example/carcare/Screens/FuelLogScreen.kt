@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.carcare.Screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carcare.viewmodels.FuelLog
import com.example.carcare.viewmodels.VehicleDashboardViewModel

@Composable
fun FuelLogScreen(navController: NavController, vehicleId: String) {
    val context = LocalContext.current
    val viewModel: VehicleDashboardViewModel = viewModel()

    // Load fuel logs when screen appears
    LaunchedEffect(vehicleId) {
        viewModel.loadFuelLogs(vehicleId)
    }

    val efficiencyData = remember(viewModel.fuelLogs) {
        calculateEfficiency(viewModel.fuelLogs)
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
            if (efficiencyData.totalDistance > 0) {
                EfficiencySummaryCard(efficiencyData)
                Spacer(modifier = Modifier.height(16.dp))
            }

            when {
                viewModel.fuelLogs.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No fuel logs yet. Add your first fuel log!")
                    }
                }
                else -> {
                    LazyColumn {
                        items(viewModel.fuelLogs) { log ->
                            FuelLogCard(
                                log = log,
                                onDelete = { viewModel.deleteFuelLog(vehicleId, log.id) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(log.date, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Amount", style = MaterialTheme.typography.bodySmall)
                    Text("${log.amount} L", style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Cost", style = MaterialTheme.typography.bodySmall)
                    Text("${log.cost} PKR", style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Odometer", style = MaterialTheme.typography.bodySmall)
                    Text("${log.odometer} km", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Notes: ${log.notes}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

data class EfficiencyData(
    val avgEfficiency: Double,
    val avgCostPerKm: Double,
    val totalDistance: Int,
    val logCount: Int
)

fun calculateEfficiency(logs: List<FuelLog>): EfficiencyData {
    if (logs.size < 2) return EfficiencyData(0.0, 0.0, 0, logs.size)

    val sortedLogs = logs.sortedBy { it.odometer }
    var totalDistance = 0
    var totalFuel = 0.0
    var totalCost = 0.0

    for (i in 1 until sortedLogs.size) {
        val distance = sortedLogs[i].odometer - sortedLogs[i - 1].odometer
        val fuel = sortedLogs[i].amount

        totalDistance += distance
        totalFuel += fuel
        totalCost += sortedLogs[i].cost
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Fuel Efficiency Summary", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Average", style = MaterialTheme.typography.bodySmall)
                    Text("${"%.2f".format(data.avgEfficiency)} km/L", style = MaterialTheme.typography.titleLarge)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Distance", style = MaterialTheme.typography.bodySmall)
                    Text("${data.totalDistance} km", style = MaterialTheme.typography.titleLarge)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cost/km", style = MaterialTheme.typography.bodySmall)
                    Text("${"%.2f".format(data.avgCostPerKm)} PKR", style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Based on ${data.logCount} fill-ups", style = MaterialTheme.typography.bodySmall)
        }
    }
}
