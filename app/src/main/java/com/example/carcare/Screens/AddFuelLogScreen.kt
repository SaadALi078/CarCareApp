@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.Screens



import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.*


@Composable
fun AddFuelLogScreen(navController: NavController, backStackEntry: NavBackStackEntry)
 {
    val context = LocalContext.current
    val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var amount by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            date = "$day/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Fuel Log") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Fuel Amount (Liters)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Cost (PKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = odometer,
                onValueChange = { odometer = it },
                label = { Text("Odometer (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (amount.isBlank() || cost.isBlank() || date.isBlank() || odometer.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }


                    val db = FirebaseFirestore.getInstance()
                    val data = mapOf(
                        "amount" to amount,
                        "cost" to cost,
                        "date" to date,
                        "odometer" to odometer,
                        "notes" to notes,
                        "timestamp" to Timestamp.now()
                    )

                    db.collection("users")
                        .document(uid)
                        .collection("vehicles")
                        .document(vehicleId)
                        .collection("fuel_logs")
                        .add(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Fuel log saved", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save log", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Fuel Log")
            }
        }
    }
}
