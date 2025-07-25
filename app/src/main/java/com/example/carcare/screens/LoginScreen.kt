package com.example.carcare.screens

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.example.carcare.R
import com.google.firebase.auth.FirebaseAuth

// Premium color scheme
private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(DeepNavy, MidnightBlue)
)
private val CardGradient = Brush.linearGradient(
    colors = listOf(CeruleanFrost.copy(alpha = 0.2f), MidnightBlue.copy(alpha = 0.7f))
)
private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(PremiumAccent, AquaCyan)
)

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Decorative elements
        Image(
            painter = painterResource(R.drawable.ic_car_silhouette),
            contentDescription = "Car Silhouette",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .alpha(0.05f)
        )

        // Main card with glass effect
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = CardGradient,
                        shape = RoundedCornerShape(32.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(PremiumAccent, AquaCyan)),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(32.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with logo
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(PremiumAccent.copy(alpha = 0.2f), Color.Transparent)
                                ),
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.radialGradient(
                                    colors = listOf(PremiumAccent, PremiumAccent.copy(alpha = 0.3f))),
                                shape = CircleShape
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Login",
                            tint = PremiumAccent,
                            modifier = Modifier.size(36.dp))
                    }

                    Text(
                        "Welcome Back",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PremiumAccent
                        )
                    )

                    Text(
                        "Sign in to continue to CarCare",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email field
                    Column {
                        Text(
                            "Email Address",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White.copy(alpha = 0.8f)),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("you@example.com", color = Color.White.copy(alpha = 0.5f)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = PremiumAccent
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MidnightBlue.copy(alpha = 0.4f),
                                unfocusedContainerColor = MidnightBlue.copy(alpha = 0.4f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = PremiumAccent.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp))
                        )
                    }

                    // Password field
                    Column {
                        Text(
                            "Password",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White.copy(alpha = 0.8f)),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Enter your password", color = Color.White.copy(alpha = 0.5f)) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = PremiumAccent
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Password Visibility",
                                        tint = PremiumAccent
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MidnightBlue.copy(alpha = 0.4f),
                                unfocusedContainerColor = MidnightBlue.copy(alpha = 0.4f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = PremiumAccent.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            loading = true
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    loading = false
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        if (user != null && user.isEmailVerified) {
                                            navController.navigate(Screen.Dashboard.route) {
                                                popUpTo(Screen.Login.route) { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, "Email not verified. Please check your inbox.", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .graphicsLayer {
                                scaleX = pulse
                                scaleY = pulse
                            }
                            .shadow(16.dp, RoundedCornerShape(16.dp), clip = true),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, PremiumAccent),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !loading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ButtonGradient, RoundedCornerShape(16.dp))
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    "LOGIN",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Links
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { navController.navigate("register") }
                        ) {
                            Text(
                                "Create Account",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PremiumAccent.copy(alpha = 0.8f)
                                )
                            )
                        }

                        TextButton(
                            onClick = { navController.navigate("forgot") }
                        ) {
                            Text(
                                "Forgot Password?",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PremiumAccent.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}