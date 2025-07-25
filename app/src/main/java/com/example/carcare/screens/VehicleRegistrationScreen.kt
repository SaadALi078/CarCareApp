package com.example.carcare.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BrandingWatermark
import androidx.compose.material.icons.filled.BrandingWatermark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.carcare.viewmodel.vehicle.VehicleFormEvent
import com.example.carcare.R

import com.example.carcare.viewmodel.vehicle.VehicleViewModel

// Premium color scheme
private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val GoldAccent = Color(0xFFFFD700)
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
fun VehicleRegistrationScreen(
    navController: NavController,
    viewModel: VehicleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when (event) {
                is VehicleFormEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is VehicleFormEvent.NavigateToDashboard -> {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.VehicleRegistration.route) { inclusive = true }
                    }
                }
            }
        }
    }

    // Animation states
    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(800))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        // Decorative car silhouette with gradient effect
        Image(
            painter = painterResource(R.drawable.ic_car_silhouette),
            contentDescription = "Car Silhouette",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(0.8f)
                .alpha(0.08f),
            colorFilter = ColorFilter.tint(PremiumAccent)
        )


        // Main content card with glass effect
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center)
                .alpha(contentAlpha.value)
                .shadow(24.dp, RoundedCornerShape(32.dp), clip = true),
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
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header with animated icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .graphicsLayer {
                                scaleX = pulse
                                scaleY = pulse
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
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
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.DirectionsCar,
                                contentDescription = "Vehicle",
                                tint = PremiumAccent,
                                modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Register Your Vehicle",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PremiumAccent
                            )
                        )
                    }

                    Text(
                        "Add your vehicle details to get started",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Premium form fields
                    PremiumVehicleField(
                        label = "Make",
                        value = state.make,
                        onValueChange = { viewModel.onFieldChange("make", it) },
                        icon = Icons.Default.BrandingWatermark
                    )

                    PremiumVehicleField(
                        label = "Model",
                        value = state.model,
                        onValueChange = { viewModel.onFieldChange("model", it) },
                        icon = Icons.Default.DirectionsCar
                    )

                    PremiumVehicleField(
                        label = "Year",
                        value = state.year,
                        onValueChange = { viewModel.onFieldChange("year", it) },
                        keyboardType = KeyboardType.Number,
                        icon = Icons.Default.Event
                    )

                    PremiumVehicleField(
                        label = "License Plate",
                        value = state.plate,
                        onValueChange = { viewModel.onFieldChange("plate", it) },
                        icon = Icons.Default.Rectangle
                    )

                    PremiumVehicleField(
                        label = "Current Mileage",
                        value = state.mileage,
                        onValueChange = { viewModel.onFieldChange("mileage", it) },
                        keyboardType = KeyboardType.Number,
                        icon = Icons.Default.Speed
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save button with loading state
                    Button(
                        onClick = { viewModel.saveVehicle() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(16.dp, RoundedCornerShape(16.dp), clip = true),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, PremiumAccent),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !state.isLoading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ButtonGradient, RoundedCornerShape(16.dp))
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Row(
                                    modifier = Modifier.align(Alignment.Center),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Save",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "SAVE VEHICLE",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Skip option
                    TextButton(
                        onClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.VehicleRegistration.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            "Skip for now",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PremiumAccent.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }
    }
}

// Premium vehicle form field component
@Composable
fun PremiumVehicleField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    icon: ImageVector
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PremiumAccent,
                modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White.copy(alpha = 0.8f))
            )
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MidnightBlue.copy(alpha = 0.4f),
                unfocusedContainerColor = MidnightBlue.copy(alpha = 0.4f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            placeholder = {
                Text(
                    "Enter $label",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.5f))
                )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = PremiumAccent.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp))
        )
    }
}