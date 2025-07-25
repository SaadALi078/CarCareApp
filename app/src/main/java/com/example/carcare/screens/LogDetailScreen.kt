@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.example.carcare.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.carcare.data.model.MaintenanceLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Reuse the same color palette from FuelLogScreen
private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val WarningOrange = Color(0xFFFF9E64)
private val BackgroundGradient = Brush.verticalGradient(colors = listOf(DeepNavy, MidnightBlue))
private val TopBarGradient = Brush.horizontalGradient(colors = listOf(CeruleanFrost, AquaCyan))
private val CardGradient = Brush.linearGradient(
    colors = listOf(
        CeruleanFrost.copy(alpha = 0.2f),
        MidnightBlue.copy(alpha = 0.8f)
    )
)

@Composable
fun LogDetailScreen(navController: NavController, vehicleId: String, logId: String) {
    val context = LocalContext.current
    var log by remember { mutableStateOf<MaintenanceLog?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseAnim"
    )

    // Load log
    LaunchedEffect(logId, vehicleId) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("vehicles")
            .document(vehicleId)
            .collection("maintenance")
            .document(logId)
            .get()
            .addOnSuccessListener { doc ->
                doc?.let {
                    log = MaintenanceLog(
                        id = doc.id,
                        type = doc.getString("type") ?: "",
                        date = doc.getString("date") ?: "",
                        mileage = (doc.getLong("mileage") ?: 0).toInt(),
                        notes = doc.getString("notes") ?: "",
                        reminderType = doc.getString("reminderType") ?: "",
                        reminderValue = doc.getString("reminderValue") ?: "",
                        vehicleId = vehicleId
                    )
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Maintenance Details",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(TopBarGradient)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = log != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                log?.let { maintenanceLog ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Main Details Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = CardGradient,
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(PremiumAccent, AquaCyan)
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    DetailRow("Type", maintenanceLog.type, Icons.Default.Build)
                                    DetailRow("Date", maintenanceLog.date, Icons.Default.Event)
                                    DetailRow("Mileage", "${maintenanceLog.mileage} km", Icons.Default.Speed)

                                    if (maintenanceLog.notes.isNotBlank()) {
                                        DetailRow("Notes", maintenanceLog.notes, Icons.Default.Notes)
                                    }

                                    if (maintenanceLog.reminderType.isNotBlank()) {
                                        DetailRow("Reminder",
                                            "${maintenanceLog.reminderType} - ${maintenanceLog.reminderValue}",
                                            Icons.Default.NotificationsActive)
                                    }
                                }
                            }
                        }

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("add_maintenance?editId=${log?.id}&vehicleId=$vehicleId")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PremiumAccent
                                )
                            ) {
                                Icon(Icons.Default.Edit, "Edit")
                                Spacer(Modifier.width(8.dp))
                                Text("Edit")
                            }

                            Button(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WarningOrange
                                )
                            ) {
                                Icon(Icons.Default.Delete, "Delete")
                                Spacer(Modifier.width(8.dp))
                                Text("Delete")
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = log == null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PremiumAccent)
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                modifier = Modifier
                    .background(
                        brush = CardGradient,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(PremiumAccent, AquaCyan)),
                        shape = RoundedCornerShape(24.dp)
                    ),
                title = {
                    Text(
                        "Delete Maintenance?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to delete this record?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .collection("vehicles")
                                .document(vehicleId)
                                .collection("maintenance")
                                .document(logId)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Maintenance deleted", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningOrange
                        )
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text(
                            "Cancel",
                            color = PremiumAccent
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = AquaCyan,
            modifier = Modifier.size(24.dp)
        )

        Column {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}