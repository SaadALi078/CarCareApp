package com.example.carcare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.carcare.Screens.*
import com.carcare.navigation.Screen
import com.example.carcare.Screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.VehicleRegistration.route) {
            VehicleRegistrationScreen(navController)
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }

        composable(
            route = "maintenance/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            MaintenanceScreen(navController, vehicleId)
        }

        composable(
            route = "add_maintenance/{vehicleId}?editId={editId}",
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType },
                navArgument("editId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            AddMaintenanceScreen(navController, backStackEntry)
        }

        composable(
            route = "fuel/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            FuelLogScreen(navController, vehicleId)
        }

        composable(
            route = "add_fuel/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            AddFuelLogScreen(navController, backStackEntry)
        }

        composable(Screen.Emergency.route) {
            EmergencyScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
