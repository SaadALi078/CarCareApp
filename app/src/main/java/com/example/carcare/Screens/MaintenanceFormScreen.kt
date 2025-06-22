package com.example.carcare.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.R
import com.example.carcare.Screens.AccentMagenta
import com.example.carcare.Screens.PrimaryPurple
import com.example.carcare.data.MaintenanceRecord
import com.example.carcare.navigation.Router
import com.example.carcare.viewmodels.MaintenanceFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceFormScreen(recordId: String? = null) {
    val viewModel: MaintenanceFormViewModel = viewModel()
    val state by viewModel.state.collectAsState()

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
                onClick = { viewModel.saveRecord(state.record) },
                containerColor = AccentMagenta,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_save), // Make sure this icon exists
                    contentDescription = "Save Record",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        } else {
            MaintenanceFormContent(
                record = state.record,
                onFieldChange = { viewModel.updateRecordField(it) },
                error = state.error,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun MaintenanceFormContent(
    record: MaintenanceRecord,
    onFieldChange: (MaintenanceRecord) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = record.type,
            onValueChange = { onFieldChange(record.copy(type = it)) },
            label = { Text("Maintenance Type") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = record.date,
            onValueChange = { onFieldChange(record.copy(date = it)) },
            label = { Text("Date (MM/DD/YYYY)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = record.mileage.toString(),
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    onFieldChange(record.copy(mileage = it.toIntOrNull() ?: 0))
                }
            },
            label = { Text("Mileage (km)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = record.cost.toString(),
            onValueChange = {
                if (it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                    onFieldChange(record.copy(cost = it.toDoubleOrNull() ?: 0.0))
                }
            },
            label = { Text("Cost") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = record.notes,
            onValueChange = { onFieldChange(record.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4
        )

        // Error message
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
