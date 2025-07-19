/*package com.example.carcare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carcare.Screens.ForgotPasswordScreen
import com.example.carcare.Screens.LoginScreen
import com.example.carcare.Screens.SignupScreen
import com.example.carcare.ui.theme.PrimaryPurple
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CarCareapp() {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }

    // Check auth state
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate("dashboard")
        } else {
            navController.navigate("login")
        }
        showSplash = false
    }

    if (showSplash) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Car Care",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { navController.navigate("dashboard") },
                    onSignupClick = { navController.navigate("signup") },
                    onForgotPasswordClick = { navController.navigate("forgot_password") }
                )
            }

            composable("signup") {
                SignupScreen(
                    onSignupSuccess = { navController.navigate("vehicle_registration") },
                    onLoginClick = { navController.popBackStack() }
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onBackClick = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            composable("vehicle_registration") {
                // Placeholder for vehicle registration
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Vehicle Registration Screen")
                }
            }

            composable("dashboard") {
                // Placeholder for dashboard
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Dashboard Screen")
                }
            }
        }
    }
}*/