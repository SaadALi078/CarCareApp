package com.example.carcare.Screens

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.example.carcare.R
import com.example.carcare.ui.theme.PremiumAccent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    // Animation states
    val scale = remember { Animatable(0.8f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    // Start animations
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(1200, easing = LinearOutSlowInEasing)
        )
        alpha.animateTo(1f, animationSpec = tween(1000))
    }

    // Navigation logic
    LaunchedEffect(true) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (uid == null) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            } else {
                db.collection("users").document(uid)
                    .collection("vehicles")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        navController.navigate(
                            if (snapshot.isEmpty) Screen.VehicleRegistration.route
                            else Screen.Dashboard.route
                        ) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                    .addOnFailureListener {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
            }
        }, 2000)
    }

    // Premium UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated logo
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale.value)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_car_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        rotationZ = rotation.value
                    }
            )


            // Animated text
        Text(
            text = "CAR CARE",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .alpha(alpha.value)
        )

        // Loading indicator with accent color
        CircularProgressIndicator(
            color = PremiumAccent,
            strokeWidth = 3.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(alpha.value)
        )
    }
}}