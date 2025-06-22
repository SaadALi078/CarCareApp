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
    object MaintenanceLogScreen : Screen()
    object MaintenanceFormScreen : Screen()

    // Screen with arguments
    data class MaintenanceLogScreenWithCategory(val category: String) : Screen()
    data class MaintenanceFormScreenWithRecord(val recordId: String?) : Screen()
}

// Singleton object for navigation
object Router {
    val currentScreen: MutableState<Screen> = mutableStateOf(Screen.Signup)
    private val backStack = mutableListOf<Screen>()

    fun navigateTo(destination: Screen) {
        backStack.add(currentScreen.value)
        currentScreen.value = destination
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen.value = backStack.removeAt(backStack.size - 1)
        }
    }

    // Specific navigation methods for maintenance
    fun navigateToMaintenanceLog(category: String? = null) {
        navigateTo(
            category?.let { Screen.MaintenanceLogScreenWithCategory(it) }
                ?: Screen.MaintenanceLogScreen
        )
    }

    fun navigateToMaintenanceForm(recordId: String? = null) {
        navigateTo(
            recordId?.let { Screen.MaintenanceFormScreenWithRecord(it) }
                ?: Screen.MaintenanceFormScreen
        )
    }
}