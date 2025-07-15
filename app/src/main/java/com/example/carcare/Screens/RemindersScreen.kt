package com.example.carcare.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carcare.R
import com.example.carcare.data.Reminder
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.ui.components.AnimatedBackground
import com.example.carcare.ui.theme.*
import com.example.carcare.viewmodels.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RemindersScreen(
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val currentReminder by viewModel.currentReminder.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.loadReminders(userId)
        }
    }

    AnimatedBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Maintenance Reminders",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { Router.navigateTo(Screen.HomeScreen) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
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
                    onClick = { viewModel.showAddReminderDialog() },
                    containerColor = PrimaryPurple,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(12.dp, shape = CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Reminder", modifier = Modifier.size(30.dp))
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when {
                    state.isLoading -> FullScreenLoader()
                    state.reminders.isEmpty() -> EmptyRemindersState { viewModel.showAddReminderDialog() }
                    else -> RemindersList(
                        reminders = state.reminders,
                        onMarkComplete = viewModel::markAsComplete,
                        onEdit = viewModel::showEditReminderDialog,
                        onDelete = viewModel::showDeleteConfirmation
                    )
                }

                state.error?.let { errorMsg ->
                    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DeepBlack.copy(alpha = 0.8f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(errorMsg, color = Color.Red, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            currentReminder?.let { reminder ->
                AddReminderDialog(
                    reminder = reminder,
                    onDismiss = viewModel::hideAddDialog,
                    onSave = {
                        viewModel.saveReminder(
                            userId = userId,
                            vehicleId = reminder.vehicleId,
                            vehicleName = reminder.vehicleName
                        )
                    },
                    onReminderChange = viewModel::setCurrentReminder
                )
            }
        }

        if (showDeleteDialog) {
            currentReminder?.let { reminder ->
                AlertDialog(
                    onDismissRequest = viewModel::hideDeleteDialog,
                    title = { Text("Delete Reminder") },
                    text = { Text("Are you sure you want to delete this reminder?") },
                    confirmButton = {
                        Button(
                            onClick = viewModel::deleteReminder,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                        ) {
                            Text("Delete", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::hideDeleteDialog) {
                            Text("Cancel", color = PrimaryPurple)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FullScreenLoader() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryPurple)
    }
}

@Composable
fun EmptyRemindersState(onAddReminder: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = "No Reminders",
            tint = PrimaryPurple,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No active reminders",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onAddReminder,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPurple,
                contentColor = Color.White
            )
        ) {
            Text("Add Reminder")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RemindersList(
    reminders: List<Reminder>,
    onMarkComplete: (Reminder) -> Unit,
    onEdit: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit
) {
    val listState = rememberLazyListState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reminders, key = { it.id }) { reminder ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(reminder.vehicleName, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(reminder.type, color = LightPurple)
                    Text("Due: ${dateFormat.format(reminder.dueDate.toDate())}", color = LightPurple)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onMarkComplete(reminder) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Done")
                        }
                        Button(
                            onClick = { onEdit(reminder) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            Text("Edit")
                        }
                        Button(
                            onClick = { onDelete(reminder) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddReminderDialog(
    reminder: Reminder,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onReminderChange: (Reminder) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add/Edit Reminder") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = reminder.type,
                    onValueChange = { onReminderChange(reminder.copy(type = it)) },
                    label = { Text("Type*") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reminder.vehicleName,
                    onValueChange = { onReminderChange(reminder.copy(vehicleName = it)) },
                    label = { Text("Vehicle Name*") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reminder.odometerThreshold.toString(),
                    onValueChange = {
                        onReminderChange(reminder.copy(odometerThreshold = it.toIntOrNull() ?: 0))
                    },
                    label = { Text("Odometer Threshold (km)*") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PrimaryPurple)
            }
        }
    )
}