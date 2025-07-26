package com.carcare.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.example.carcare.Component.PremiumAccent
import com.example.carcare.R
import com.google.firebase.auth.FirebaseAuth

private val CeruleanFrost = Color(0xFF1A2980)
private val MidnightBlue = Color(0xFF1D1D2F)
private val AquaCyan = Color(0xFF26D0CE)
private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(PremiumAccent, AquaCyan)
)
private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(MidnightBlue, Color.Black)
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

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(800))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_car_silhouette),
            contentDescription = "Car Silhouette",
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.BottomCenter)
                .alpha(0.15f)
                .offset(y = 80.dp)
        )

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(600)),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center)
                    .graphicsLayer {
                        alpha = contentAlpha.value
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(24.dp, RoundedCornerShape(32.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    CeruleanFrost.copy(alpha = 0.2f),
                                    MidnightBlue.copy(alpha = 0.7f)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(PremiumAccent, AquaCyan)),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(32.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                        colors = listOf(PremiumAccent, PremiumAccent.copy(alpha = 0.3f))
                                    ),
                                    shape = CircleShape
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_car_logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Welcome Back",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PremiumAccent
                            )
                        )

                        Text(
                            "Sign in to continue to CarCare",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = AquaCyan.copy(alpha = 0.8f)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        AuthField(
                            label = "Email Address",
                            placeholder = "you@example.com",
                            value = email,
                            onValueChange = { email = it },
                            icon = Icons.Default.Email,
                            isPassword = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AuthField(
                            label = "Password",
                            placeholder = "Enter your password",
                            value = password,
                            onValueChange = { password = it },
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onPasswordToggle = { passwordVisible = !passwordVisible }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Don't have an account? ",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            )
                            Text(
                                "Sign Up",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AquaCyan
                                ),
                                modifier = Modifier
                                    .clickable { navController.navigate(Screen.Register.route) }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isPassword: Boolean,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color.White.copy(alpha = 0.8f)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.5f)
                    )
                )
            },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = AquaCyan,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { onPasswordToggle?.invoke() }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Password Visibility",
                            tint = AquaCyan
                        )
                    }
                }
            } else null,
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
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            PremiumAccent.copy(alpha = 0.3f),
                            AquaCyan.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        )
    }
}
