@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.example.carcare.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.carcare.utils.ReminderUtils
import com.example.carcare.workers.ReminderWorker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.concurrent.TimeUnit

// Reuse the premium color scheme
private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val GoldAccent = Color(0xFFFFD700)
private val WarningOrange = Color(0xFFFF9E64)
private val SuccessGreen = Color(0xFF4CAF50)
private val BackgroundGradient = Brush.verticalGradient(colors = listOf(DeepNavy, MidnightBlue))
private val TopBarGradient = Brush.horizontalGradient(colors = listOf(CeruleanFrost, AquaCyan))
private val CardGradient = Brush.linearGradient(colors = listOf(CeruleanFrost.copy(alpha = 0.2f), MidnightBlue.copy(alpha = 0.7f)))
private val ButtonGradient = Brush.horizontalGradient(colors = listOf(PremiumAccent, AquaCyan))

@Composable
fun AddMaintenanceScreen(
    navController: NavController,
    vehicleId: String,
    editId: String?
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseAnim"
    )

    val isEditMode = editId != null
    val maintenanceTypes = listOf("Oil Change", "Tire Rotation", "Brake Check", "Battery Check", "General Service")
    var selectedType by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var mileageText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    // Date and Time Picker setup
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = ReminderUtils.formatDate(dayOfMonth, month, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            selectedTime = ReminderUtils.formatTime(hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Load existing data for edit mode
    LaunchedEffect(editId) {
        if (isEditMode) {
            db.collection("users").document(uid)
                .collection("vehicles").document(vehicleId)
                .collection("maintenance").document(editId!!)
                .get()
                .addOnSuccessListener { doc ->
                    selectedType = doc.getString("type") ?: ""
                    selectedDate = doc.getString("date") ?: ""
                    selectedTime = doc.getString("time") ?: ""
                    mileageText = doc.getLong("mileage")?.toString() ?: ""
                    notes = doc.getString("notes") ?: ""
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Maintenance" else "Add Maintenance",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(TopBarGradient)
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(24.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = CardGradient,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(PremiumAccent, AquaCyan)),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Title
                        Text(
                            "Maintenance Details",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PremiumAccent
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Maintenance Type Dropdown
                        Column {
                            Text(
                                "Maintenance Type",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .background(
                                            color = MidnightBlue.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = PremiumAccent.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            selectedType.ifEmpty { "Select type" },
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = if (selectedType.isEmpty()) Color.White.copy(alpha = 0.5f) else Color.White
                                            )
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "Dropdown",
                                            tint = PremiumAccent
                                        )
                                    }
                                }
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(MidnightBlue)
                                ) {
                                    maintenanceTypes.forEach {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    it,
                                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                                )
                                            },
                                            onClick = {
                                                selectedType = it
                                                expanded = false
                                            },
                                            modifier = Modifier.background(MidnightBlue)
                                        )
                                    }
                                }
                            }
                        }

                        // Date & Time Fields
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Date Picker
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Date",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White.copy(alpha = 0.7f)
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                InputFieldWithIcon(
                                    value = selectedDate,
                                    placeholder = "Select date",
                                    onClick = { datePicker.show() }
                                )
                            }

                            // Time Picker
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Time",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White.copy(alpha = 0.7f)
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                InputFieldWithIcon(
                                    value = selectedTime,
                                    placeholder = "Select time",
                                    onClick = { timePicker.show() }
                                )
                            }
                        }

                        // Mileage Input
                        Column {
                            Text(
                                "Mileage (km)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            TextField(
                                value = mileageText,
                                onValueChange = { mileageText = it },
                                placeholder = { Text("Current mileage", color = Color.White.copy(alpha = 0.5f)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MidnightBlue.copy(alpha = 0.5f),
                                    unfocusedContainerColor = MidnightBlue.copy(alpha = 0.5f),
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
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            )
                        }

                        // Notes
                        Column {
                            Text(
                                "Notes",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            TextField(
                                value = notes,
                                onValueChange = { notes = it },
                                placeholder = { Text("Additional notes...", color = Color.White.copy(alpha = 0.5f)) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MidnightBlue.copy(alpha = 0.5f),
                                    unfocusedContainerColor = MidnightBlue.copy(alpha = 0.5f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .border(
                                        width = 1.dp,
                                        color = PremiumAccent.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            )
                        }

                        // Save Button
                        Button(
                            onClick = {
                                val mileage = mileageText.toIntOrNull()
                                if (selectedType.isBlank() || selectedDate.isBlank() || selectedTime.isBlank() || mileage == null) {
                                    Toast.makeText(context, "Please fill all required fields correctly", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val data = mapOf(
                                    "type" to selectedType,
                                    "date" to selectedDate,
                                    "time" to selectedTime,
                                    "mileage" to mileage,
                                    "notes" to notes,
                                    "vehicleId" to vehicleId,
                                    "timestamp" to Timestamp.now()
                                )

                                val logsRef = db.collection("users").document(uid)
                                    .collection("vehicles").document(vehicleId)
                                    .collection("maintenance")

                                val onComplete = {
                                    Toast.makeText(context, "Maintenance ${if (isEditMode) "updated" else "saved"}", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }

                                if (isEditMode) {
                                    logsRef.document(editId!!).set(data).addOnSuccessListener { onComplete() }
                                } else {
                                    logsRef.add(data).addOnSuccessListener { onComplete() }
                                }

                                // Reminder Scheduling
                                val delay = ReminderUtils.calculateDelayFromDateTime(selectedDate, selectedTime)
                                if (delay > 0L) {
                                    val message = "Reminder: $selectedType on $selectedDate at $selectedTime"
                                    val inputData = Data.Builder().putString("message", message).build()

                                    val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                        .setInputData(inputData)
                                        .build()

                                    WorkManager.getInstance(context).enqueue(request)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .graphicsLayer {
                                    scaleX = pulse
                                    scaleY = pulse
                                },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, PremiumAccent),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                if (isEditMode) "Update Maintenance" else "Save Maintenance",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputFieldWithIcon(value: String, placeholder: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MidnightBlue.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = PremiumAccent.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                value.ifEmpty { placeholder },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (value.isEmpty()) Color.White.copy(alpha = 0.5f) else Color.White
                )
            )
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = "Select",
                tint = PremiumAccent
            )
        }
    }
}