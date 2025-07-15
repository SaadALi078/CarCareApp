package com.example.carcare.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.navigation.Router
import com.example.carcare.ui.components.MaintenanceRecordItem
import com.example.carcare.viewmodels.MaintenanceLogViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceLogScreen(
    vehicleId: String,
    selectedCategory: String? = null
) {
    val viewModel: MaintenanceLogViewModel = viewModel()
    val state = viewModel.state.collectAsState().value
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    LaunchedEffect(selectedCategory) {
        viewModel.loadRecords(selectedCategory)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedCategory != null)
                            "$selectedCategory Log"
                        else
                            "Maintenance Log"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { Router.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Router.navigateToMaintenanceForm(vehicleId)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Text(
                    text = "Error: ${state.error}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
            state.records.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No maintenance records found")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.records) { record ->
                        MaintenanceRecordItem(
                            record = record.copy(date = dateFormat.format(SimpleDateFormat("MM/dd/yyyy").parse(record.date))),
                            onClick = {
                                Router.navigateToMaintenanceForm(
                                    vehicleId = vehicleId,
                                    recordId = record.id
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}