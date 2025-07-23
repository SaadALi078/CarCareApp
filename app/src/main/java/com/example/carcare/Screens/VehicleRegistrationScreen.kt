package com.example.carcare.Screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.carcare.viewmodel.vehicle.VehicleFormEvent
import com.example.carcare.R
import com.example.carcare.ui.theme.PremiumAccent
import com.example.carcare.ui.theme.PremiumSurfaceVariant
import com.example.carcare.viewmodel.vehicle.VehicleViewModel

@Composable
fun VehicleRegistrationScreen(
    navController: NavController,
    viewModel: VehicleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

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

    // Premium UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Decorative car silhouette
        Image(
            painter = painterResource(R.drawable.ic_car_silhouette),
            contentDescription = "Car Silhouette",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(0.8f)
                .alpha(0.1f)
        )

        // Main content card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center)
                .alpha(contentAlpha.value),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = PremiumSurfaceVariant.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.DirectionsCar,
                        contentDescription = "Vehicle",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Register Your Vehicle",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    "Add your vehicle details to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Premium form fields
                PremiumVehicleField(
                    label = "Make",
                    value = state.make,
                    onValueChange = { viewModel.onFieldChange("make", it) },
                    iconRes = R.drawable.ic_make
                )

                PremiumVehicleField(
                    label = "Model",
                    value = state.model,
                    onValueChange = { viewModel.onFieldChange("model", it) },
                    iconRes = R.drawable.ic_model
                )

                PremiumVehicleField(
                    label = "Year",
                    value = state.year,
                    onValueChange = { viewModel.onFieldChange("year", it) },
                    keyboardType = KeyboardType.Number,
                    iconRes = R.drawable.ic_calendar
                )

                PremiumVehicleField(
                    label = "License Plate",
                    value = state.plate,
                    onValueChange = { viewModel.onFieldChange("plate", it) },
                    iconRes = R.drawable.ic_plate
                )

                PremiumVehicleField(
                    label = "Current Mileage",
                    value = state.mileage,
                    onValueChange = { viewModel.onFieldChange("mileage", it) },
                    keyboardType = KeyboardType.Number,
                    iconRes = R.drawable.ic_mileage
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save button with loading state
                Button(
                    onClick = { viewModel.saveVehicle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            "Save Vehicle",
                            style = MaterialTheme.typography.labelLarge
                        )
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
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
    iconRes: Int? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = iconRes?.let {
            {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}