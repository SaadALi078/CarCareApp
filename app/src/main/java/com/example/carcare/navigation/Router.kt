package com.example.carcare.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// ----------------------
// Sealed Class for Screens
// ----------------------
sealed class Screen(val route: String) {

    // Authentication Screens
    object Signup : Screen("signup")
    object TermsAndConditionsScreen : Screen("terms")
    object LoginScreen : Screen("login")
    object ForgetPasswordScreen : Screen("forget_password")

    // Main App Screens
    object HomeScreen : Screen("home")
    object VehiclesScreen : Screen("vehicles")
    object RemindersScreen : Screen("reminders")
    object ProfileScreen : Screen("profile")
    object NotificationsScreen : Screen("notifications")
    object EmergencyHelpScreen : Screen("emergency_help")
    object MaintenanceScreen : Screen("maintenance") // base screen with no vehicle selected

    // ------------------
    // Maintenance Screens With Parameters
    // ------------------
    data class MaintenanceScreenWithVehicle(val vehicleId: String) :
        Screen("maintenance/$vehicleId") {
        companion object {
            fun createRoute(vehicleId: String) = "maintenance/$vehicleId"
        }
    }

    data class MaintenanceLogScreenWithVehicle(val vehicleId: String) :
        Screen("maintenance_log/$vehicleId") {
        companion object {
            fun createRoute(vehicleId: String) = "maintenance_log/$vehicleId"
        }
    }

    data class MaintenanceLogScreenWithCategory(
        val vehicleId: String,
        val category: String
    ) : Screen("maintenance_log/$vehicleId/$category") {
        companion object {
            fun createRoute(vehicleId: String, category: String) =
                "maintenance_log/$vehicleId/$category"
        }
    }

    data class MaintenanceFormScreenWithVehicle(
        val vehicleId: String,
        val recordId: String? = null
    ) : Screen(
        if (recordId != null)
            "maintenance_form/$vehicleId/$recordId"
        else
            "maintenance_form/$vehicleId"
    ) {
        companion object {
            fun createRoute(vehicleId: String, recordId: String? = null): String {
                return if (recordId != null)
                    "maintenance_form/$vehicleId/$recordId"
                else
                    "maintenance_form/$vehicleId"
            }
        }
    }
}

// ----------------------
// Router Object to Navigate Screens
// ----------------------
object Router {
    private val backStack = mutableListOf<Screen>()
    var currentScreen = mutableStateOf<Screen>(Screen.LoginScreen)

    fun navigateTo(destination: Screen) {
        backStack.add(currentScreen.value)
        currentScreen.value = destination
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen.value = backStack.removeAt(backStack.lastIndex)
        }
    }


    fun clearBackStack() {
        backStack.clear()
    }

    // Helpers
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
}

// ----------------------
// Utility Parsers
// ----------------------

fun parseVehicleIdFromRoute(route: String): String? {
    return route.split("/").getOrNull(1)
}

fun parseMaintenanceCategory(route: String): String? {
    return route.split("/").getOrNull(2)
}

fun parseMaintenanceRecordId(route: String): String? {
    return route.split("/").getOrNull(2)
}
