package com.example.carcare.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class Screen(val route: String) {
    // Authentication Screens
    object Signup : Screen("signup")
    object TermsAndConditionsScreen : Screen("terms")
    object LoginScreen : Screen("login")
    object ForgetPasswordScreen : Screen("forget_password")

    // Core Screens
    object HomeScreen : Screen("home")
    object NotificationsScreen : Screen("notifications")
    object EmergencyHelpScreen : Screen("emergency_help")
    object ProfileScreen : Screen("profile")
    object RemindersScreen : Screen("reminders")
    object VehiclesScreen : Screen("vehicles")

    // Maintenance Screens with parameters
    data class MaintenanceScreenWithVehicle(val vehicleId: String) :
        Screen("maintenance/{vehicleId}") {
        companion object {
            const val ROUTE = "maintenance"
            fun createRoute(vehicleId: String) = "$ROUTE/$vehicleId"
        }
    }

    data class MaintenanceLogScreenWithVehicle(val vehicleId: String) :
        Screen("maintenance_log/{vehicleId}") {
        companion object {
            const val ROUTE = "maintenance_log"
            fun createRoute(vehicleId: String) = "$ROUTE/$vehicleId"
        }
    }

    data class MaintenanceLogScreenWithCategory(val vehicleId: String, val category: String) :
        Screen("maintenance_log/{vehicleId}/{category}") {
        companion object {
            const val ROUTE = "maintenance_log"
            fun createRoute(vehicleId: String, category: String) = "$ROUTE/$vehicleId/$category"
        }
    }

    data class MaintenanceFormScreenWithVehicle(val vehicleId: String, val recordId: String? = null) :
        Screen(
            if (recordId != null)
                "maintenance_form/{vehicleId}/{recordId}"
            else
                "maintenance_form/{vehicleId}"
        ) {
        companion object {
            const val ROUTE = "maintenance_form"
            fun createRoute(vehicleId: String, recordId: String? = null): String =
                if (recordId != null) "$ROUTE/$vehicleId/$recordId" else "$ROUTE/$vehicleId"
        }
    }
}

object Router {
    private val backStack = mutableListOf<Screen>()

    // ✅ Public readable, private writable state
    var currentScreen by mutableStateOf<Screen>(Screen.LoginScreen)
        private set

    fun navigateTo(destination: Screen) {
        backStack.add(currentScreen)
        currentScreen = destination
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeAt(backStack.lastIndex)
        }
    }

    fun clearBackStack() {
        backStack.clear()
    }

    // ✅ Convenient navigation helpers
    fun navigateToMaintenance(vehicleId: String) {
        navigateTo(Screen.MaintenanceScreenWithVehicle(vehicleId))
    }

    fun navigateToMaintenanceLog(vehicleId: String, category: String? = null) {
        if (category != null) {
            navigateTo(Screen.MaintenanceLogScreenWithCategory(vehicleId, category))
        } else {
            navigateTo(Screen.MaintenanceLogScreenWithVehicle(vehicleId))
        }
    }

    fun navigateToMaintenanceForm(vehicleId: String, recordId: String? = null) {
        navigateTo(Screen.MaintenanceFormScreenWithVehicle(vehicleId, recordId))
    }

    fun navigateToEmergencyHelp() {
        navigateTo(Screen.EmergencyHelpScreen)
    }

    fun navigateToVehicles() {
        navigateTo(Screen.VehiclesScreen)
    }

    fun navigateToReminders() {
        navigateTo(Screen.RemindersScreen)
    }

    fun navigateToProfile() {
        navigateTo(Screen.ProfileScreen)
    }

    // Route Parsing Helpers (if needed)
    fun parseVehicleIdFromRoute(route: String): String? =
        route.split("/").getOrNull(1)

    fun parseMaintenanceCategory(route: String): String? =
        route.split("/").getOrNull(2)

    fun parseMaintenanceRecordId(route: String): String? =
        route.split("/").getOrNull(2)
}
