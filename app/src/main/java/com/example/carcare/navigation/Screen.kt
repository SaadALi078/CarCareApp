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


    object Emergency : Screen("emergency")
    object Profile : Screen("profile")
    object Fuel : Screen("fuel?vehicleId={vehicleId}") {
        fun withVehicleId(id: String) = "fuel?vehicleId=$id"
    }
    object FuelLog : Screen("fuel") {
        fun withVehicleId(id: String): String = "fuel/$id"
    }

    object AddFuelLog : Screen("add_fuel_log?vehicleId={vehicleId}") {
        fun withVehicleId(id: String) = "add_fuel_log?vehicleId=$id"
    }

}

