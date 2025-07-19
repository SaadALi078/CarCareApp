package com.carcare.navigation



sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Forgot : Screen("forgot")
    object VehicleRegistration : Screen("vehicle_registration")
    object Dashboard : Screen("dashboard")

    object Maintenance : Screen("maintenance") {
        fun withVehicleId(vehicleId: String) = "maintenance/$vehicleId"
    }

    object AddMaintenance : Screen("add_maintenance")
    object LogDetail : Screen("log_detail")

    object Fuel : Screen("fuel")
    object Emergency : Screen("emergency")
    object Profile : Screen("profile")
}

