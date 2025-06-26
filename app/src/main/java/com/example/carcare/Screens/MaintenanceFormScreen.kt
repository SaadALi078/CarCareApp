package com.example.carcare.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.R
import com.example.carcare.data.MaintenanceRecord
import com.example.carcare.navigation.Router
import com.example.carcare.ui.components.showDatePickerDialog
import com.example.carcare.viewmodels.MaintenanceFormViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceFormScreen(
    vehicleId: String,
    recordId: String? = null
) {
    val viewModel: MaintenanceFormViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Load record if editing
    LaunchedEffect(recordId) {
        recordId?.let { viewModel.loadRecord(it) }
    }

    // Handle save success
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            Router.navigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (recordId == null) "Add Maintenance" else "Edit Maintenance",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { Router.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPurple
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.validateAndSave(vehicleId) },
                containerColor = AccentMagenta,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_save),
                    contentDescription = "Save Record",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        } else {
            MaintenanceFormContent(
                record = state.record,
                onFieldChange = { viewModel.updateRecordField(it) },
                error = state.error,
                typeError = state.typeError,
                dateError = state.dateError,
                mileageError = state.mileageError,
                costError = state.costError,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun MaintenanceFormContent(
    record: MaintenanceRecord,
    onFieldChange: (MaintenanceRecord) -> Unit,
    error: String?,
    typeError: String?,
    dateError: String?,
    mileageError: String?,
    costError: String?,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Maintenance Type Field
        OutlinedTextField(
            value = record.type,
            onValueChange = { onFieldChange(record.copy(type = it)) },
            label = { Text("Maintenance Type*") },
            isError = typeError != null,
            supportingText = {
                typeError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Date Field with Picker
        OutlinedTextField(
            value = record.date,
            onValueChange = { },
            label = { Text("Date*") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Pick date"
                    )
                }
            },
            isError = dateError != null,
            supportingText = {
                dateError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )

        // Mileage Field
        OutlinedTextField(
            value = if (record.mileage > 0) record.mileage.toString() else "",
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    val mileage = it.toIntOrNull() ?: 0
                    onFieldChange(record.copy(mileage = mileage))
                }
            },
            label = { Text("Mileage (km)*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = mileageError != null,
            supportingText = {
                mileageError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Cost Field
        OutlinedTextField(
            value = if (record.cost > 0) "%.2f".format(record.cost) else "",
            onValueChange = {
                if (it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                    val cost = it.toDoubleOrNull() ?: 0.0
                    onFieldChange(record.copy(cost = cost))
                }
            },
            label = { Text("Cost*") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            isError = costError != null,
            supportingText = {
                costError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Notes Field
        OutlinedTextField(
            value = record.notes,
            onValueChange = { onFieldChange(record.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4
        )

        // General error message
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        showDatePickerDialog(
            context = context,
            onDateSelected = { date ->
                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                onFieldChange(record.copy(date = dateFormat.format(date)))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}