@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.Screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
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

@Composable
fun AddMaintenanceScreen(
    navController: NavController,
    vehicleId: String,
    editId: String?
)
 {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return


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
            TopAppBar(title = { Text(if (isEditMode) "Edit Maintenance" else "Add Maintenance") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Maintenance Type Dropdown
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedType,
                    onValueChange = {},
                    label = { Text("Maintenance Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    maintenanceTypes.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                selectedType = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Date & Time Fields
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Reminder Date") },
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Pick Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = selectedTime,
                onValueChange = {},
                readOnly = true,
                label = { Text("Reminder Time") },
                trailingIcon = {
                    IconButton(onClick = { timePicker.show() }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Pick Time")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Mileage Input
            OutlinedTextField(
                value = mileageText,
                onValueChange = { mileageText = it },
                label = { Text("Mileage") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Update Log" else "Save Maintenance")
            }
        }
    }
}
