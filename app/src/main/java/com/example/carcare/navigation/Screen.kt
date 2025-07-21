package com.carcare.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Forgot : Screen("forgot")
    object VehicleRegistration : Screen("vehicle_registration")
    object Dashboard : Screen("dashboard")

    object Maintenance : Screen("maintenance/{vehicleId}") {
        fun withVehicleId(vehicleId: String) = "maintenance/$vehicleId"
    }

    object AddMaintenance : Screen("add_maintenance?vehicleId={vehicleId}&editId={editId}") {
        fun withArgs(vehicleId: String, editId: String? = null): String {
            return "add_maintenance?vehicleId=$vehicleId" + if (editId != null) "&editId=$editId" else ""
        }
    }

    object LogDetail : Screen("log_detail/{vehicleId}/{logId}") {
        fun withArgs(vehicleId: String, logId: String): String = "log_detail/$vehicleId/$logId"
    }

    object Emergency : Screen("emergency")
    object Profile : Screen("profile")

    object FuelLog : Screen("fuel/{vehicleId}") {
        fun withVehicleId(id: String): String = "fuel/$id"
    }

    object AddFuelLog : Screen("add_fuel/{vehicleId}") {
        fun withVehicleId(id: String) = "add_fuel/$id"
    }
}
