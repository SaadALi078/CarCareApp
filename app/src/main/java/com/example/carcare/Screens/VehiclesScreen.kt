package com.example.carcare.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.data.Vehicle
import com.example.carcare.data.VehiclesViewModel
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.ui.components.AnimatedBackground
import com.example.carcare.ui.components.EmptyState
import com.example.carcare.ui.components.VehicleCard
import com.example.carcare.ui.components.AddEditVehicleDialog
import com.example.carcare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VehiclesScreen() {
    val viewModel: VehiclesViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVehicles()
    }

    AnimatedBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "My Vehicles",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { Router.navigateTo(Screen.HomeScreen) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle settings */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DeepBlack.copy(alpha = 0.7f)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddVehicleDialog = true },
                    containerColor = PrimaryPurple,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Vehicle",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryPurple)
                        }
                    }

                    state.vehicles.isEmpty() -> {
                        EmptyState(
                            icon = Icons.Default.DirectionsCar,
                            title = "No Vehicles Added",
                            message = "Add your first vehicle to get started",
                            actionText = "Add Vehicle",
                            onAction = { viewModel.showAddVehicleDialog = true }
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(
                                items = state.vehicles,
                                key = { it.id }
                            ) { vehicle ->
                                VehicleCard(
                                    vehicle = vehicle,
                                    onClick = {
                                        Router.navigateTo(Screen.MaintenanceScreenWithVehicle(vehicle.id))
                                    },
                                    onEdit = { viewModel.startEditing(vehicle) },
                                    onDelete = { viewModel.deleteVehicle(vehicle.id) }
                                )
                            }
                        }
                    }
                }

                if (viewModel.showAddVehicleDialog) {
                    AddEditVehicleDialog(
                        vehicle = viewModel.editingVehicle,
                        onDismiss = {
                            viewModel.showAddVehicleDialog = false
                            viewModel.editingVehicle = null
                        },
                        onSave = { vehicleData ->
                            if (viewModel.editingVehicle != null) {
                                viewModel.updateVehicle(vehicleData)
                            } else {
                                viewModel.addVehicle(vehicleData)
                            }
                            viewModel.showAddVehicleDialog = false
                            viewModel.editingVehicle = null
                        }
                    )
                }
            }
        }
    }
}