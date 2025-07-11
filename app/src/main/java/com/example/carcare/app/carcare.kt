package com.example.carcare.app
import androidx.compose.runtime.collectAsState

import ForgetPasswordScreen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.data.VehiclesViewModel
import com.example.carcare.screens.RemindersScreen
import com.example.carcare.Screens.*
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen

@Composable
fun CarCareapp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = Router.currentScreen.value) { screen ->
            when (screen) {
                // Auth Screens
                is Screen.LoginScreen -> LoginScreen()
                is Screen.Signup -> SignupScreen()
                is Screen.TermsAndConditionsScreen -> TermsAndConditionsScreen()

                is Screen.ForgetPasswordScreen -> ForgetPasswordScreen {
                    Router.navigateTo(Screen.LoginScreen)
                }

                // Main Screens
                is Screen.HomeScreen -> HomeScreen()
                is Screen.NotificationsScreen -> NotificationsScreen()
                is Screen.EmergencyHelpScreen -> EmergencyHelpScreen()
                is Screen.ProfileScreen -> ProfileScreen()
                is Screen.RemindersScreen -> RemindersScreen()
                is Screen.VehiclesScreen -> VehiclesScreen()

                // Maintenance Screens
                is Screen.MaintenanceScreen -> {
                    val viewModel: VehiclesViewModel = viewModel()
                    val state by viewModel.state.collectAsState()
                    var hasRedirected by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        viewModel.loadVehicles()
                    }

                    LaunchedEffect(state.vehicles) {
                        if (state.vehicles.isNotEmpty() && !hasRedirected) {
                            hasRedirected = true
                            Router.navigateTo(Screen.MaintenanceScreenWithVehicle(state.vehicles[0].id))
                        }
                    }

                    if (state.vehicles.isEmpty() && !state.isLoading) {
                        Text("Please add a vehicle first", color = Color.DarkGray)
                    } else if (state.isLoading) {
                        Text("Loading vehicles...", color = Color.DarkGray)
                    }
                }

                is Screen.MaintenanceScreenWithVehicle -> {
                    MaintenanceScreen(vehicleId = screen.vehicleId)
                }

                is Screen.MaintenanceLogScreenWithVehicle -> {
                    MaintenanceLogScreen(vehicleId = screen.vehicleId)
                }

                is Screen.MaintenanceLogScreenWithCategory -> {
                    MaintenanceLogScreen(
                        vehicleId = screen.vehicleId,
                        selectedCategory = screen.category
                    )
                }

                is Screen.MaintenanceFormScreenWithVehicle -> {
                    MaintenanceFormScreen(
                        vehicleId = screen.vehicleId,
                        recordId = screen.recordId
                    )
                }

                // Fallback
                else -> {
                    Text("Unknown screen: ${screen::class.simpleName}", color = Color.Red)
                }
            }
        }
    }
}