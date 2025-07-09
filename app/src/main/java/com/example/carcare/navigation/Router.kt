package com.example.carcare.navigation

import androidx.compose.runtime.mutableStateOf

sealed class Screen(val route: String) {
    object Signup : Screen("signup")
    object TermsAndConditionsScreen : Screen("terms")
    object LoginScreen : Screen("login")
    object ForgetPasswordScreen : Screen("forget_password")

    object HomeScreen : Screen("home")
    object VehiclesScreen : Screen("vehicles")
    object RemindersScreen : Screen("reminders")
    object ProfileScreen : Screen("profile")
    object NotificationsScreen : Screen("notifications")
    object EmergencyHelpScreen : Screen("emergency_help")
    object MaintenanceScreen : Screen("maintenance")

    data class MaintenanceScreenWithVehicle(val vehicleId: String) :
        Screen("maintenance/$vehicleId")

    data class MaintenanceLogScreenWithVehicle(val vehicleId: String) :
        Screen("maintenance_log/$vehicleId")

    data class MaintenanceLogScreenWithCategory(
        val vehicleId: String,
        val category: String
    ) : Screen("maintenance_log/$vehicleId/$category")

    data class MaintenanceFormScreenWithVehicle(
        val vehicleId: String,
        val recordId: String? = null
    ) : Screen(
        if (recordId != null)
            "maintenance_form/$vehicleId/$recordId"
        else
            "maintenance_form/$vehicleId"
    )
}

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

    fun clearBackStack() = backStack.clear()

    fun navigateToMaintenance(vehicleId: String) {
        navigateTo(Screen.MaintenanceScreenWithVehicle(vehicleId))
    }

    fun navigateToMaintenanceLog(vehicleId: String, category: String? = null) {
        if (vehicleId.isBlank()) {
            println("Error: Missing vehicleId")
            return
        }
        if (category != null) {
            navigateTo(Screen.MaintenanceLogScreenWithCategory(vehicleId, category))
        } else {
            navigateTo(Screen.MaintenanceLogScreenWithVehicle(vehicleId))
        }
    }

    fun navigateToMaintenanceForm(vehicleId: String, recordId: String? = null) {
        if (vehicleId.isBlank()) {
            println("Error: Missing vehicleId")
            return
        }
        navigateTo(Screen.MaintenanceFormScreenWithVehicle(vehicleId, recordId))
    }
}

fun parseVehicleIdFromRoute(route: String): String? = route.split("/").getOrNull(1)
fun parseMaintenanceCategory(route: String): String? = route.split("/").getOrNull(2)
fun parseMaintenanceRecordId(route: String): String? = route.split("/").getOrNull(2)
