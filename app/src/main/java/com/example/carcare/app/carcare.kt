package com.example.carcare.app

import ForgetPasswordScreen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import com.example.carcare.screens.MaintenanceLogScreenUI

import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.screens.*

@Composable
fun carcare() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        Crossfade(targetState = Router.currentScreen) { currentState ->
            when (currentState.value) {
                is Screen.Signup -> {
                    SignupScreen()
                }
                is Screen.TermsAndCondtionsScreen -> {
                    TermsAndConditionsScreen()
                }
                is Screen.LoginScreen -> {
                    LoginScreen()
                }
                is Screen.HomeScreen -> {
                    HomeScreen()
                }
                is Screen.ForgetPasswordScreen -> {
                    ForgetPasswordScreen(
                        onNavigateBack = { Router.navigateTo(Screen.LoginScreen) }
                    )
                }
                is Screen.NotificationsScreen -> {
                    NotificationsScreen()
                }
                is Screen.EmergencyHelpScreen -> {
                    EmergencyHelpScreen()
                }
                is Screen.MaintenanceLogScreen -> {
                    MaintenanceLogScreenUI()
                }

                is Screen.MaintenanceScreen -> {
                    MaintenanceScreen()
                }
                is Screen.ProfileScreen -> {
                    ProfileScreen()
                }
                is Screen.RemindersScreen -> {
                    RemindersScreen()
                }
                is Screen.VehiclesScreen -> {
                    VehiclesScreen()
                }

                else -> {
                    Text("Unknown screen", color = Color.Red)
                }
            }
        }
    }
}
