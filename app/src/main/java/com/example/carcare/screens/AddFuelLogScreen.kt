@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.screens
import com.example.carcare.Component.customTextFieldColors

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carcare.viewmodels.VehicleDashboardViewModel
import java.util.*

private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0F0F1B), Color(0xFF1D1D2F))
)
private val TopBarGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
)
private val CardColor = Color(0xFF1E223C)
private val AccentColor = Color(0xFF4A7BFF)
private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(AccentColor, Color(0xFF6A93FF))
)

@Composable
fun AddFuelLogScreen(navController: NavController, vehicleId: String) {
    val context = LocalContext.current
    val viewModel: VehicleDashboardViewModel = viewModel()

    var amount by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day -> date = "$day/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Fuel Log",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                modifier = Modifier.background(TopBarGradient)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .background(BackgroundGradient)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Fuel Log Details",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .padding(vertical = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardColor, RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(AccentColor.copy(alpha = 0.3f), AccentColor.copy(alpha = 0.1f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    FuelInputField("Fuel Amount (Liters)", amount) { amount = it }
                    FuelInputField("Cost (PKR)", cost) { cost = it }

                    Column {
                        Text("Date", color = Color.White.copy(alpha = 0.8f))
                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = { datePicker.show() },
                                    modifier = Modifier
                                        .background(AccentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .padding(6.dp)
                                ) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AccentColor)
                                }
                            },
                            colors = customTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    FuelInputField("Odometer (km)", odometer) { odometer = it }

                    Column {
                        Text("Notes (optional)", color = Color.White.copy(alpha = 0.8f))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            colors = customTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 4
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (amount.isBlank() || cost.isBlank() || date.isBlank() || odometer.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    val costValue = cost.toDoubleOrNull() ?: 0.0
                    val odometerValue = odometer.toIntOrNull() ?: 0

                    viewModel.addFuelLog(
                        vehicleId = vehicleId,
                        amount = amountValue,
                        cost = costValue,
                        date = date,
                        odometer = odometerValue,
                        notes = notes,
                        onSuccess = {
                            navController.popBackStack()
                            Toast.makeText(context, "Fuel log saved", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed to save: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
                    .height(60.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp), clip = true),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ButtonGradient, RoundedCornerShape(16.dp))
                ) {
                    Text(
                        "SAVE FUEL LOG",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun FuelInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = customTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}



