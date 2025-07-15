package com.example.carcare.Screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.R
import com.example.carcare.data.MaintenanceDashboardViewModel
import com.example.carcare.data.MaintenanceCategory
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.ui.components.AnimatedBackground
import com.example.carcare.ui.components.EmptyState
import com.example.carcare.ui.components.MaintenanceRecordItem
import com.example.carcare.ui.theme.*
import com.example.carcare.data.VehiclesViewModel
import com.example.carcare.data.Vehicle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MaintenanceScreen(vehicleId: String) {
    val context = LocalContext.current
    val viewModel: MaintenanceDashboardViewModel = viewModel()
    val vehiclesViewModel: VehiclesViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val vehicleState by vehiclesViewModel.state.collectAsState()

    // Handle no vehicle selected
    if (vehicleId.isBlank()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Please select a vehicle first", Toast.LENGTH_SHORT).show()
            Router.navigateTo(Screen.VehiclesScreen)
        }
        return
    }

    // Get current vehicle
    val currentVehicle = remember(vehicleId) {
        vehicleState.vehicles.find { it.id == vehicleId }
    }

    // If vehicle not found after loading
    if (!vehicleState.isLoading && currentVehicle == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Vehicle not found", Toast.LENGTH_SHORT).show()
            Router.navigateTo(Screen.VehiclesScreen)
        }
        return Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Vehicle not found. Redirecting...", color = Color.White)
        }
    }

    // Set vehicle when available
    LaunchedEffect(currentVehicle) {
        currentVehicle?.let { viewModel.setVehicle(it) }
    }

    AnimatedBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "${currentVehicle?.name ?: "Vehicle"} Maintenance",
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
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
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
                    onClick = {
                        Router.navigateToMaintenanceForm(
                            vehicleId = vehicleId
                        )
                    },
                    containerColor = PrimaryPurple,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Record",
                        modifier = Modifier.size(30.dp)
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
            } else if (currentVehicle == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading vehicle...", color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard(
                                "Upcoming",
                                state.upcomingCount.toString(),
                                Color(0xFFFF9800),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
                            StatCard(
                                "Overdue",
                                state.overdueCount.toString(),
                                Color(0xFFF44336),
                                modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Maintenance Categories",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .height(300.dp)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.categories.size) { index ->
                                val category = state.categories[index]
                                MaintenanceCategoryCard(
                                    category = category,
                                    onClick = {
                                        viewModel.selectCategory(category.name)
                                        Router.navigateToMaintenanceLog(
                                            vehicleId = vehicleId,
                                            category = category.name
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Recent Maintenance",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                        )
                    }

                    items(state.recentMaintenance) { record ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically()
                        ) {
                            MaintenanceRecordItem(
                                record = record,
                                onClick = {
                                    Router.navigateToMaintenanceForm(
                                        vehicleId = vehicleId,
                                        recordId = record.id
                                    )
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border =
            BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    PrimaryPurple.copy(alpha = 0.7f),
                    AccentMagenta.copy(alpha = 0.7f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    color = color,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    color = LightPurple,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MaintenanceCategoryCard(
    category: MaintenanceCategory,
    onClick: () -> Unit
) {
    val alpha by rememberInfiniteTransition().animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(PrimaryPurple, AccentMagenta))
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryPurple.copy(alpha = alpha * 0.3f),
                            AccentMagenta.copy(alpha = alpha * 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = category.iconRes),
                    contentDescription = category.name,
                    tint = LightPurple,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.name,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}