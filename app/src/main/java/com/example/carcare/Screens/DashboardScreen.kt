@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.Screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carcare.navigation.Screen
import com.example.carcare.viewmodels.VehicleDashboardViewModel
import com.example.carcare.viewmodels.VehicleItem

@Composable
fun DashboardScreen(navController: NavController) {
    val viewModel: VehicleDashboardViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Car Care", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Drawer or Settings */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, state.vehicles.firstOrNull()?.id)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Your Vehicles", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            VehicleCarousel()

            Spacer(modifier = Modifier.height(24.dp))
            Text("Quick Access", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            QuickAccessCards(navController, state.vehicles.firstOrNull()?.id)
        }
    }
}

@Composable
fun VehicleCarousel(viewModel: VehicleDashboardViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.vehicles.isEmpty() -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No vehicles added yet.")
            }
        }

        else -> {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.vehicles.forEach { vehicle ->
                    VehicleCard(vehicle)
                }
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: VehicleItem) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${vehicle.make} ${vehicle.model}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Mileage: ${vehicle.mileage}", fontSize = 14.sp)
        }
    }
}

@Composable
fun QuickAccessCards(navController: NavController, vehicleId: String?) {
    val cards = listOf(
        Triple("Maintenance", Icons.Default.Build, vehicleId?.let { Screen.Maintenance.withVehicleId(it) } ?: Screen.Dashboard.route),
        Triple("Fuel Logs", Icons.Default.LocalGasStation, Screen.Fuel.route),
        Triple("Emergency", Icons.Default.Warning, Screen.Emergency.route),
        Triple("Profile", Icons.Default.Person, Screen.Profile.route)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        cards.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { (label, icon, route) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clickable {
                                navController.navigate(route)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(label)
                        }
                    }
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, vehicleId: String?) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, Screen.Dashboard.route),
        Triple("Maintenance", Icons.Default.Build, vehicleId?.let { Screen.Maintenance.withVehicleId(it) } ?: Screen.Dashboard.route),
        Triple("Fuel", Icons.Default.LocalGasStation, Screen.Fuel.route),
        Triple("Emergency", Icons.Default.Warning, Screen.Emergency.route),
        Triple("Profile", Icons.Default.Person, Screen.Profile.route)
    )

    NavigationBar {
        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = false,
                onClick = {
                    navController.navigate(route)
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}
