@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.screens // This MUST be lowercase to match the folder structure.

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carcare.R
import com.example.carcare.data.model.MaintenanceLog
import com.example.carcare.viewmodels.MaintenanceViewModel
import kotlin.random.Random

// Premium Color Palette
private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val GoldAccent = Color(0xFFFFD700)
private val WarningColor = Color(0xFFFF9E64)

private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(DeepNavy, MidnightBlue)
)
private val TopBarGradient = Brush.horizontalGradient(
    colors = listOf(CeruleanFrost, AquaCyan)
)
private val CardColor = Color(0xFF1E223C)
private val GlassEffect = Brush.linearGradient(
    colors = listOf(PremiumAccent.copy(alpha = 0.1f), Color.Transparent)
)

@Composable
fun MaintenanceScreen(navController: NavController, vehicleId: String) {
    val viewModel: MaintenanceViewModel = viewModel()
    val logs by viewModel.logs.collectAsState()

    LaunchedEffect(vehicleId) {
        viewModel.loadLogs(vehicleId)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        // Decorative particles
        MaintenanceParticleBackground(particleCount = 20)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Maintenance Logs",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.8.sp
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.background(TopBarGradient)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("add_maintenance?vehicleId=$vehicleId")
                    },
                    containerColor = PremiumAccent,
                    contentColor = Color.White,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = pulse
                            scaleY = pulse
                        }
                        .shadow(16.dp, RoundedCornerShape(50))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Maintenance",
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (logs.isEmpty()) {
                MaintenanceEmptyState()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        CeruleanFrost.copy(alpha = 0.3f),
                                        AquaCyan.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Maintenance History",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(logs, key = { it.id }) { log ->
                            MaintenanceCard(
                                log = log,
                                onClick = {
                                    navController.navigate("add_maintenance?vehicleId=$vehicleId&editId=${log.id}")
                                },
                                onDelete = {
                                    viewModel.deleteLog(vehicleId, log.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceParticleBackground(particleCount: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        repeat(particleCount) {
            val x = Random.nextFloat() * size.width
            val y = Random.nextFloat() * size.height
            val size = Random.nextInt(1, 4).dp.toPx()
            val alpha = (Random.nextFloat() * 0.2f) + 0.1f
            drawCircle(
                color = PremiumAccent.copy(alpha = alpha),
                radius = size,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun MaintenanceCard(log: MaintenanceLog, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = PremiumAccent),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassEffect, RoundedCornerShape(28.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(PremiumAccent.copy(alpha = 0.4f), PremiumAccent.copy(alpha = 0.1f))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(PremiumAccent.copy(alpha = 0.3f), Color.Transparent)
                                ),
                                shape = RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                brush = Brush.radialGradient(
                                    colors = listOf(PremiumAccent, PremiumAccent.copy(alpha = 0.3f))),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(getIconForType(log.type)),
                            contentDescription = log.type,
                            tint = PremiumAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        log.type,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        IconButton(
                            onClick = onClick,
                            modifier = Modifier
                                .size(40.dp)
                                .background(PremiumAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = PremiumAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(40.dp)
                                .background(WarningColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = WarningColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "MILEAGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            "${log.mileage} km",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PremiumAccent
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "DATE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            log.date,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        )
                    }
                }
                if (log.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "NOTES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        log.notes,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MaintenanceEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
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
                Icons.Default.Build,
                contentDescription = "No Maintenance",
                tint = PremiumAccent,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "No Maintenance Records",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Keep your vehicle in top condition by tracking all maintenance activities",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.7f)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Tap the + button to add your first record",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PremiumAccent.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        )
    }
}

fun getIconForType(type: String): Int {
    return when (type.lowercase()) {
        "oil change" -> R.drawable.ic_oil
        "tire rotation" -> R.drawable.ic_tire
        "brake check" -> R.drawable.ic_brake
        "fluid top-up" -> R.drawable.ic_fluid
        else -> R.drawable.ic_maintenance
    }
}