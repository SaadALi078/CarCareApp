package com.example.carcare.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import com.example.carcare.Screens.EmergencyHelpScreen

import com.example.carcare.Screens.HomeScreen
import com.example.carcare.Screens.LoginScreen
import com.example.carcare.Screens.MaintenanceFormScreen
import com.example.carcare.Screens.MaintenanceLogScreen
import com.example.carcare.Screens.MaintenanceScreen
import com.example.carcare.Screens.NotificationsScreen
import com.example.carcare.Screens.ProfileScreen
import com.example.carcare.Screens.SignupScreen
import com.example.carcare.Screens.TermsAndConditionsScreen
import com.example.carcare.Screens.VehiclesScreen
import com.example.carcare.screens.RemindersScreen

import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.navigation.Screen.ForgetPasswordScreen


@Composable
fun CarCareApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = Router.currentScreen.value) { screen ->
            when (screen) {
                is Screen.LoginScreen -> LoginScreen()
                is Screen.Signup -> SignupScreen()
                is Screen.TermsAndConditionsScreen -> TermsAndConditionsScreen()
                is Screen.ForgetPasswordScreen -> ForgetPasswordScreen {
                    Router.navigateTo(Screen.LoginScreen)
                }

                is Screen.HomeScreen -> HomeScreen()
                is Screen.NotificationsScreen -> NotificationsScreen()
                is Screen.EmergencyHelpScreen -> EmergencyHelpScreen()
                is Screen.ProfileScreen -> ProfileScreen()
                is Screen.RemindersScreen -> RemindersScreen()
                is Screen.VehiclesScreen -> VehiclesScreen()

                is Screen.MaintenanceScreen -> {
                    Text("Please select a vehicle to continue", color = Color.DarkGray)
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

                else -> {
                    Text("Unknown screen: ${screen::class.simpleName}", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ForgetPasswordScreen(content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}
