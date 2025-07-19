package com.example.carcare.Screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.carcare.viewmodel.vehicle.VehicleFormEvent

import com.example.carcare.viewmodel.vehicle.VehicleViewModel


@Composable
fun VehicleRegistrationScreen(
    navController: NavController,
    viewModel: VehicleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Register Your Vehicle", style = typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))

            VehicleInputField("Make", state.make) {
                viewModel.onFieldChange("make", it)
            }

            VehicleInputField("Model", state.model) {
                viewModel.onFieldChange("model", it)
            }

            VehicleInputField("Year", state.year, KeyboardType.Number) {
                viewModel.onFieldChange("year", it)
            }

            VehicleInputField("License Plate", state.plate) {
                viewModel.onFieldChange("plate", it)
            }

            VehicleInputField("Current Mileage", state.mileage, KeyboardType.Number) {
                viewModel.onFieldChange("mileage", it)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.saveVehicle() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                Text("Save Vehicle")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.VehicleRegistration.route) { inclusive = true }
                    }
                }
            ) {
                Text("Skip for now", style = typography.bodyMedium)
            }

            AnimatedVisibility(visible = state.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun VehicleInputField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp)
    )
}
