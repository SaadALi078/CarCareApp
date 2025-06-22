package com.example.carcare.app

import ForgetPasswordScreen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.carcare.Screens.EmergencyHelpScreen
import com.example.carcare.Screens.HomeScreen
import com.example.carcare.Screens.NotificationsScreen
import com.example.carcare.Screens.ProfileScreen
import com.example.carcare.Screens.RemindersScreen
import com.example.carcare.Screens.SignupScreen
import com.example.carcare.Screens.TermsAndConditionsScreen
import com.example.carcare.Screens.VehiclesScreen
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.navigation.Screen.ForgetPasswordScreen
import com.example.carcare.screens.* // Import all screens from the screens package

@Composable
fun CarCareApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = Router.currentScreen.value) { screen ->
            when (screen) {
                is Screen.Signup -> SignupScreen()
                is Screen.TermsAndCondtionsScreen -> TermsAndConditionsScreen()
                is Screen.LoginScreen -> LoginScreen()
                is Screen.HomeScreen -> HomeScreen()
                is Screen.ForgetPasswordScreen -> ForgetPasswordScreen {
                    Router.navigateTo(Screen.LoginScreen)
                }
                is Screen.NotificationsScreen -> NotificationsScreen()
                is Screen.EmergencyHelpScreen -> EmergencyHelpScreen()
                is Screen.MaintenanceLogScreen -> MaintenanceLogScreen()
                is Screen.MaintenanceLogScreenWithCategory -> MaintenanceLogScreen(selectedCategory = screen.category)
                is Screen.MaintenanceFormScreen -> MaintenanceFormScreen(recordId = null)
                is Screen.MaintenanceFormScreenWithRecord -> MaintenanceFormScreen(recordId = screen.recordId)
                is Screen.MaintenanceScreen -> MaintenanceScreen()
                is Screen.ProfileScreen -> ProfileScreen()
                is Screen.RemindersScreen -> RemindersScreen()
                is Screen.VehiclesScreen -> VehiclesScreen()
                else -> Text("Unknown screen: ${screen::class.simpleName}", color = Color.Red)
            }
        }
    }
}