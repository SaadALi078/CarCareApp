package com.example.carcare.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.data.MaintenanceDashboardViewModel
import com.example.carcare.data.MaintenanceCategory
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.ui.components.AnimatedBackground
import com.example.carcare.ui.components.MaintenanceRecordItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(vehicleId: String) { // Add vehicleId parameter
    val viewModel: MaintenanceDashboardViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Set vehicle ID when screen is first composed
    LaunchedEffect(vehicleId) {
        viewModel.setVehicleId(vehicleId)
    }

    AnimatedBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    AccentMagenta.copy(alpha = 0.7f),
                                    PrimaryPurple.copy(alpha = 0.7f),
                                    DeepBlack.copy(alpha = 0.5f)
                                )
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { Router.navigateTo(Screen.HomeScreen) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp))
                        }

                        Text(
                            text = "Maintenance Hub",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        Router.navigateToMaintenanceForm(
                            vehicleId = vehicleId // Use passed vehicleId
                        )
                    },
                    containerColor = PrimaryPurple,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(60.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(colors = listOf(PrimaryPurple, AccentMagenta)),
                            shape = CircleShape
                        )
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
                                screenWidth * 0.4f
                            )
                            StatCard(
                                "Overdue",
                                state.overdueCount.toString(),
                                Color(0xFFF44336),
                                screenWidth * 0.4f
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Maintenance Categories",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                        )

                        val gridHeight = (120 * 3 + 16 * 2).dp

                        Box(
                            modifier = Modifier
                                .height(gridHeight)
                                .padding(horizontal = 16.dp)
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                userScrollEnabled = true
                            ) {
                                items(state.categories.size) { index ->
                                    val category = state.categories[index]
                                    MaintenanceCategoryCard(
                                        category = category,
                                        onClick = {
                                            viewModel.selectCategory(category.name)
                                            Router.navigateToMaintenanceLog(
                                                vehicleId = vehicleId, // Use passed vehicleId
                                                category = category.name
                                            )
                                        }
                                    )
                                }
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
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            MaintenanceRecordItem(
                                record = record,
                                onClick = {
                                    Router.navigateToMaintenanceForm(
                                        vehicleId = vehicleId, // Use passed vehicleId
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
    width: Dp
) {
    Card(
        modifier = Modifier
            .width(width)
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MaintenanceScreenPreview() {
    MaintenanceScreen(vehicleId = "test_vehicle_id")
}