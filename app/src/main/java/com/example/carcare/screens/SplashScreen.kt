package com.example.carcare.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.example.carcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Premium color scheme
private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(DeepNavy, MidnightBlue)
)

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    // Animation states
    val scale = remember { Animatable(0.8f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

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

    // Premium UI with glass effect
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        // Decorative elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PremiumAccent.copy(alpha = 0.1f), Color.Transparent),
                        radius = 500f
                    )
                )
        )

        // Main circular container with image inside
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(CeruleanFrost.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.radialGradient(
                        colors = listOf(PremiumAccent, PremiumAccent.copy(alpha = 0.3f))),
                    shape = CircleShape
                )
                .scale(scale.value)
        ) {
            // Image placed inside the circular container
            Image(
                painter = painterResource(R.drawable.ic_car_logo),
                contentDescription = "App Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize(0.7f)  // Adjust size to fit inside the circle
                    .align(Alignment.Center)
                    .graphicsLayer {
                        rotationZ = rotation.value
                    }
                    .alpha(alpha.value)
            )
        }

        // Text and progress indicator outside the circle
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            // Animated text
            Text(
                text = "CAR CARE",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PremiumAccent
                ),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .alpha(alpha.value)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
            )

            // Loading indicator with accent color
            CircularProgressIndicator(
                color = PremiumAccent,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(32.dp)
                    .alpha(alpha.value)
            )
        }
    }
}