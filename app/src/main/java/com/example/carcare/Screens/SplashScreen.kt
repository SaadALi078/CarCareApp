package com.example.carcare.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.carcare.navigation.Screen

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import com.example.carcare.R

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.isEmailVerified) {
            val uid = user.uid
            val hasVehicle = checkIfUserHasVehicle(uid)
            if (hasVehicle) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            } else {
                navController.navigate(Screen.VehicleRegistration.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    // UI part
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo), // make sure you have logo.png or svg in res/drawable
                contentDescription = "App Logo"
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Car Care", style = MaterialTheme.typography.titleLarge)
        }
    }
}

suspend fun checkIfUserHasVehicle(uid: String): Boolean {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("vehicles")
            .limit(1)
            .get()
            .await()

        !snapshot.isEmpty
    } catch (e: Exception) {
        Log.e("SplashScreen", "Vehicle check failed: ${e.message}")
        false // fallback: treat as if no vehicles
    }
}
