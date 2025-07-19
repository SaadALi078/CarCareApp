package com.example.carcare.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.carcare.Screens.ForgotPasswordScreen
import com.carcare.Screens.LoginScreen
import com.carcare.Screens.RegisterScreen
import com.carcare.navigation.Screen
import com.example.carcare.Screens.*
import com.example.carcare.Screens.MaintenanceScreen


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Forgot.route) { ForgotPasswordScreen(navController) }
        composable(Screen.VehicleRegistration.route) { VehicleRegistrationScreen(navController) }
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }

        composable(
            route = Screen.Maintenance.route + "/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            MaintenanceScreen(navController, vehicleId)
        }




        composable(Screen.Fuel.route) {
            PlaceholderScreen("Fuel Logs")
        }
        composable(Screen.Emergency.route) {
            PlaceholderScreen("Emergency Help")
        }
        composable(Screen.Profile.route) {
            PlaceholderScreen("Profile")
        }

        composable(
            route = Screen.AddMaintenance.route + "?editId={editId}&vehicleId={vehicleId}",
            arguments = listOf(
                navArgument("editId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("vehicleId") {
                    type = NavType.StringType
                }
            )
        )
        { backStackEntry ->
            AddMaintenanceScreen(navController, backStackEntry)
        }

        composable(
            route = "${Screen.LogDetail.route}/{vehicleId}/{logId}",
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType },
                navArgument("logId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val logId = backStackEntry.arguments?.getString("logId") ?: ""
            LogDetailScreen(navController, logId, vehicleId)
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$name Screen (Coming Soon)", style = MaterialTheme.typography.titleLarge)
    }
}
