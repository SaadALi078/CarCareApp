@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carcare.Component.customTextFieldColors
import com.example.carcare.viewmodels.VehicleDashboardViewModel
import java.util.*

// Enhanced color scheme with more depth
private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val GoldAccent = Color(0xFFFFD700)
private val CardColor = Color(0x301E223C) // More transparent for glass effect
private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(DeepNavy, MidnightBlue)
)
private val TopBarGradient = Brush.horizontalGradient(
    colors = listOf(CeruleanFrost, AquaCyan)
)
private val CardGradient = Brush.linearGradient(
    colors = listOf(CeruleanFrost.copy(alpha = 0.2f), MidnightBlue.copy(alpha = 0.7f))
)
private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(PremiumAccent, AquaCyan)
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
                        modifier = Modifier.padding(start = 16.dp))
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
                modifier = Modifier
                    .background(TopBarGradient)
                    .shadow(elevation = 8.dp)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Animated title with icon
                Row(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = pulse
                            scaleY = pulse
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalGasStation,
                        contentDescription = "Fuel",
                        tint = PremiumAccent,
                        modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Fuel Log Details",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                // Main card container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(24.dp, RoundedCornerShape(32.dp)),
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
                                brush = Brush.horizontalGradient(
                                    listOf(PremiumAccent.copy(alpha = 0.7f), AquaCyan.copy(alpha = 0.7f))
                                ),
                                shape = RoundedCornerShape(32.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(28.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Input fields with improved styling
                            FuelInputField(
                                label = "Fuel Amount (Liters)",
                                value = amount,
                                icon = Icons.Default.LocalGasStation,
                                onValueChange = { amount = it }
                            )

                            FuelInputField(
                                label = "Cost (PKR)",
                                value = cost,
                                icon = Icons.Default.AttachMoney,
                                onValueChange = { cost = it }
                            )

                            // Date field with custom styling
                            Column {
                                Text(
                                    "Date",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color.White.copy(alpha = 0.8f)),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MidnightBlue.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(16.dp))
                                        .border(
                                            width = 1.dp,
                                            color = PremiumAccent.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(16.dp))
                                        .clickable { datePicker.show() }
                                        .padding(horizontal = 16.dp, vertical = 16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            if (date.isEmpty()) "Select date" else date,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = if (date.isEmpty()) Color.White.copy(alpha = 0.5f) else Color.White
                                            )
                                        )
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = "Select date",
                                            tint = PremiumAccent
                                        )
                                    }
                                }
                            }

                            FuelInputField(
                                label = "Odometer (km)",
                                value = odometer,
                                icon = Icons.Default.Speed,
                                onValueChange = { odometer = it }
                            )

                            // Notes field with custom styling
                            Column {
                                Text(
                                    "Notes (optional)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color.White.copy(alpha = 0.8f)),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                TextField(
                                    value = notes,
                                    onValueChange = { notes = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .background(
                                            color = MidnightBlue.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(16.dp))
                                        .border(
                                            width = 1.dp,
                                            color = PremiumAccent.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(16.dp)),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    placeholder = {
                                        Text("Additional notes...", color = Color.White.copy(alpha = 0.5f))
                                    },
                                    maxLines = 4
                                )
                            }
                        }
                    }
                }

                // Save button with enhanced animation
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
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                        .height(60.dp)
                        .graphicsLayer {
                            scaleX = pulse
                            scaleY = pulse
                        }
                        .shadow(16.dp, RoundedCornerShape(16.dp), clip = true),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, PremiumAccent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ButtonGradient, RoundedCornerShape(16.dp))
                    ) {
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
                                "SAVE FUEL LOG",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FuelInputField(
    label: String,
    value: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    "Enter ${label.substringBefore("(")}",
                    color = Color.White.copy(alpha = 0.5f)
                )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = PremiumAccent.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp))
        )
    }
}