package com.example.carcare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.carcare.navigation.Screen
import com.carcare.screens.ForgotPasswordScreen
import com.carcare.screens.LoginScreen
import com.carcare.screens.RegisterScreen
import com.example.carcare.screens.*


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Forgot.route) { ForgotPasswordScreen(navController) }
        composable(Screen.VehicleRegistration.route) { VehicleRegistrationScreen(navController) }
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }

        // 🆘 Emergency Map Screen with osmdroid (no permissions needed here)
        composable(Screen.Emergency.route) {
            EmergencyScreen(onBack = { navController.popBackStack() })
        }


        // 🚗 Maintenance Logs List
        composable(
            route = Screen.Maintenance.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            MaintenanceScreen(navController, vehicleId)
        }

        // 🛠️ Add / Edit Maintenance
        composable(
            route = "add_maintenance?vehicleId={vehicleId}&editId={editId}",
            arguments = listOf(
                navArgument("vehicleId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("editId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val editId = backStackEntry.arguments?.getString("editId")
            AddMaintenanceScreen(navController, vehicleId, editId)
        }

        // 📋 Maintenance Log Detail
        composable(
            route = Screen.LogDetail.route,
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType },
                navArgument("logId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            val logId = backStackEntry.arguments?.getString("logId") ?: return@composable
            LogDetailScreen(navController, vehicleId, logId)
        }

        // ⛽ Fuel Logs
        composable(
            route = Screen.FuelLog.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            FuelLogScreen(navController, vehicleId)
        }

        // ➕ Add Fuel Log
        composable(
            route = Screen.AddFuelLog.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            AddFuelLogScreen(navController, vehicleId)
        }
    }
}
