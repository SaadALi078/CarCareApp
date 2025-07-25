@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.example.carcare.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carcare.viewmodels.FuelLog
import com.example.carcare.viewmodels.VehicleDashboardViewModel

private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val GoldAccent = Color(0xFFFFD700)
private val PremiumAccent = Color(0xFF4A7BFF)
private val WarningOrange = Color(0xFFFF9E64)

private val BackgroundGradient = Brush.verticalGradient(colors = listOf(DeepNavy, MidnightBlue))
private val TopBarGradient = Brush.horizontalGradient(colors = listOf(CeruleanFrost, AquaCyan))
private val CardGradient = Brush.linearGradient(colors = listOf(Color(0x261A2980), Color(0x2626D0CE)))
private val StatGradient = Brush.horizontalGradient(colors = listOf(PremiumAccent, AquaCyan))

@Composable
fun FuelLogScreen(navController: NavController, vehicleId: String) {
    val viewModel: VehicleDashboardViewModel = viewModel()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseAnim"
    )

    LaunchedEffect(vehicleId) {
        viewModel.loadFuelLogs(vehicleId)
    }

    val efficiencyData by remember(viewModel.fuelLogs) {
        derivedStateOf { calculateEfficiencySummary(viewModel.fuelLogs) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Fuel Logs",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(TopBarGradient)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_fuel/$vehicleId") },
                containerColor = PremiumAccent,
                shape = CircleShape,
                modifier = Modifier.shadow(12.dp, CircleShape).graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(padding)
        ) {
            if (viewModel.fuelLogs.isEmpty()) {
                FuelLogEmptyState()
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    AnimatedVisibility(
                        visible = efficiencyData.totalDistance > 0,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        EfficiencySummaryCard(efficiencyData)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)
                    ) {
                        itemsIndexed(viewModel.fuelLogs, key = { _, log -> log.id }) { index, log ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500, index * 50)) +
                                        fadeIn(animationSpec = tween(500, index * 50))
                            ) {
                                FuelLogItemCard(log = log, onDelete = {
                                    viewModel.deleteFuelLog(vehicleId, log.id)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FuelLogItemCard(log: FuelLog, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x201E223C)),
        border = BorderStroke(1.dp, PremiumAccent.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CeruleanFrost.copy(alpha = 0.1f),
                            MidnightBlue
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    log.date,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .background(WarningOrange.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        Icons.Default.Delete,
                        "Delete log",
                        tint = WarningOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoColumn("Amount", "${log.amount} L", AquaCyan)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                InfoColumn("Cost", "${log.cost} PKR", AquaCyan)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                InfoColumn("Odometer", "${log.odometer} km", AquaCyan)
            }

            // Notes section
            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Notes:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    log.notes,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
fun RowScope.InfoColumn(label: String, value: String, valueColor: Color = Color.White) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.8.sp
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        )
    }
}

@Composable
fun EfficiencySummaryCard(data: EfficiencyData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CeruleanFrost.copy(alpha = 0.2f),
                            MidnightBlue.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(PremiumAccent, AquaCyan)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "PERFORMANCE SUMMARY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PremiumAccent
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SummaryStat(Icons.Default.Speed, "Avg Efficiency", "${"%.1f".format(data.avgEfficiency)} km/L")
                    SummaryStat(Icons.Default.MonetizationOn, "Avg Cost", "${"%.1f".format(data.avgCostPerKm)}/km")
                    SummaryStat(Icons.Default.Route, "Distance", "${data.totalDistance} km")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(1.dp)
                        .background(PremiumAccent.copy(alpha = 0.3f))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Based on ${data.logCount} fuel logs",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}

@Composable
fun RowScope.SummaryStat(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = StatGradient,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun FuelLogEmptyState(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PremiumAccent.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.radialGradient(
                        colors = listOf(PremiumAccent, PremiumAccent.copy(alpha = 0.3f))),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.LocalGasStation,
                contentDescription = "No Logs",
                tint = PremiumAccent,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "No Fuel Logs Yet",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Track your fuel efficiency by adding your first log",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.7f)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Tap the + button to get started",
            style = MaterialTheme.typography.bodySmall.copy(
                color = PremiumAccent.copy(alpha = 0.8f)
            )
        )
    }
}

data class EfficiencyData(
    val avgEfficiency: Double,
    val avgCostPerKm: Double,
    val totalDistance: Int,
    val logCount: Int
)

private fun calculateEfficiencySummary(logs: List<FuelLog>): EfficiencyData {
    if (logs.size < 2) return EfficiencyData(0.0, 0.0, 0, logs.size)
    val sortedLogs = logs.sortedBy { it.odometer }
    var totalDistance = 0
    var totalFuel = 0.0
    var totalCost = 0.0
    for (i in 1 until sortedLogs.size) {
        val distance = sortedLogs[i].odometer - sortedLogs[i - 1].odometer
        if (distance > 0) {
            totalDistance += distance
            totalFuel += sortedLogs[i].amount
            totalCost += sortedLogs[i].cost
        }
    }
    val avgEfficiency = if (totalFuel > 0) totalDistance / totalFuel else 0.0
    val avgCostPerKm = if (totalDistance > 0) totalCost / totalDistance else 0.0
    return EfficiencyData(avgEfficiency, avgCostPerKm, totalDistance, logs.size)
}