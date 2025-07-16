package com.example.carcare.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.carcare.data.Vehicle
import com.example.carcare.ui.theme.PrimaryPurple
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleDialog(
    vehicle: Vehicle?,
    onDismiss: () -> Unit,
    onSave: (Vehicle) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(vehicle?.name ?: "") }
    var numberPlate by remember { mutableStateOf(vehicle?.numberPlate ?: "") }
    var model by remember { mutableStateOf(vehicle?.model ?: "") }
    var year by remember { mutableStateOf(vehicle?.year?.toString() ?: "") }
    var mileage by remember { mutableStateOf(vehicle?.currentMileage?.toString() ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank() || numberPlate.isBlank() || model.isBlank() || year.isBlank() || mileage.isBlank()) {
                        errorMessage = "Please fill in all fields"
                        return@TextButton
                    }
                    val yearInt = year.toIntOrNull()
                    val mileageInt = mileage.toIntOrNull()
                    if (yearInt == null || mileageInt == null) {
                        errorMessage = "Year and Mileage must be numbers"
                        return@TextButton
                    }

                    val updatedVehicle = Vehicle(
                        id = vehicle?.id ?: UUID.randomUUID().toString(),
                        name = name.trim(),
                        numberPlate = numberPlate.trim(),
                        model = model.trim(),
                        year = yearInt,
                        currentMileage = mileageInt
                    )

                    onSave(updatedVehicle)
                    Toast.makeText(context, "Vehicle saved", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Save", color = PrimaryPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (vehicle == null) "Add Vehicle" else "Edit Vehicle",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryPurple
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Vehicle Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = numberPlate,
                    onValueChange = { numberPlate = it },
                    label = { Text("Number Plate") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Current Mileage (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                errorMessage?.let {
                    Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}
