package com.example.carcare.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.R
import com.example.carcare.Screens.AccentMagenta
import com.example.carcare.Screens.CardBackground
import com.example.carcare.Screens.DeepBlack
import com.example.carcare.Screens.LightPurple
import com.example.carcare.Screens.PrimaryPurple
import com.example.carcare.data.MaintenanceRecord
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
 // Make sure this imports your theme colors
import com.example.carcare.viewmodels.MaintenanceLogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceLogScreen(selectedCategory: String? = null) {
    val viewModel: MaintenanceLogViewModel = viewModel()
    // FIX 1: Use property delegate for state observation
    val state =viewModel.state

    // FIX 2: Properly handle category changes
    LaunchedEffect(selectedCategory) {
        viewModel.loadRecords(selectedCategory)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedCategory?.let { "$it Maintenance" } ?: "All Maintenance",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { Router.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            // FIX 3: Use proper navigation function
            FloatingActionButton(
                onClick = { Router.navigateTo(Screen.MaintenanceFormScreen) },
                containerColor = AccentMagenta,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            }

            state.records.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No maintenance records found",
                        color = LightPurple,
                        fontSize = 18.sp
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(DeepBlack),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.records) { record ->
                        // FIX 4: Pass record ID to form screen
                        MaintenanceRecordItem(
                            record = record,
                            onClick = {
                                Router.navigateTo(
                                    Screen.MaintenanceFormScreenWithRecord(record.id)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceRecordItem(
    record: MaintenanceRecord,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(PrimaryPurple, AccentMagenta)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_wrench),
                    contentDescription = "Maintenance",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = record.type,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mileage: ${record.mileage} km",
                    color = LightPurple,
                    fontSize = 14.sp
                )
                Text(
                    text = "Date: ${record.date}",
                    color = LightPurple.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "$${"%.2f".format(record.cost)}",
                color = if (record.cost > 0) LightPurple else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}