package com.example.carcare.Screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    // Start splash timer
    LaunchedEffect(true) {
        Handler(Looper.getMainLooper()).postDelayed({

            if (uid == null) {
                // Not logged in
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            } else {
                // Check if user has registered vehicles
                db.collection("users").document(uid)
                    .collection("vehicles")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) {
                            // No vehicle registered
                            navController.navigate(Screen.VehicleRegistration.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            // Vehicles exist
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                    .addOnFailureListener {
                        // In case of error, fallback to Login
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
            }

        }, 2000) // Delay for splash animation (2 seconds)
    }

    // UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Car Care", style = MaterialTheme.typography.titleLarge)
        }
    }
}
