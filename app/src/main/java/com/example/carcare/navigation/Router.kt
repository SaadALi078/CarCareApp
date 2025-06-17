package com.example.carcare.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// All app screens listed here
sealed class Screen {
    object Signup : Screen()
    object TermsAndCondtionsScreen : Screen()
    object LoginScreen : Screen()
    object HomeScreen : Screen()
    object ForgetPasswordScreen : Screen()
    object VehiclesScreen : Screen()
    object MaintenanceScreen : Screen()
    object RemindersScreen : Screen()
    object ProfileScreen : Screen()
    object NotificationsScreen : Screen()
    object EmergencyHelpScreen : Screen()

}

// Singleton object for navigation
object Router {
    val currentScreen: MutableState<Screen> = mutableStateOf(Screen.Signup)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}
