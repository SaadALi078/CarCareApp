@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.carcare.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.example.carcare.viewmodels.ProfileViewModel
import com.example.carcare.utils.SettingsKeys

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState().value
    val showDialog = viewModel.showLogoutDialog.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Profile", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isEditing) {
                OutlinedTextField(
                    value = viewModel.tempName,
                    onValueChange = { viewModel.tempName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.tempPhone,
                    onValueChange = { viewModel.tempPhone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                ProfileInfoItem("Name", uiState.name)
                ProfileInfoItem("Email", uiState.email)
                ProfileInfoItem("Phone", uiState.phone)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (uiState.isEditing) {
                            viewModel.updateProfile()
                        } else {
                            viewModel.toggleEditMode()
                        }
                    },
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        imageVector = if (uiState.isEditing) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uiState.isEditing) "Save" else "Edit")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Settings", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            SettingSwitch(
                title = "Maintenance Reminders",
                checked = uiState.maintenanceReminders,
                onCheckedChange = { viewModel.updateSetting(SettingsKeys.MAINTENANCE_REMINDERS, it) }
            )

            SettingSwitch(
                title = "Fuel Reminders",
                checked = uiState.fuelReminders,
                onCheckedChange = { viewModel.updateSetting(SettingsKeys.FUEL_REMINDERS, it) }
            )

            SettingRadioGroup(
                title = "Units",
                options = listOf("Metric (km, L)", "Imperial (miles, gal)"),
                selectedOption = if (uiState.unitSystem == "metric") 0 else 1,
                onOptionSelected = {
                    val unit = if (it == 0) "metric" else "imperial"
                    viewModel.updateSetting(SettingsKeys.UNIT_SYSTEM, unit)
                }
            )

            SettingSwitch(
                title = "Dark Theme",
                checked = uiState.darkTheme == true, // this ensures it's a Boolean
                onCheckedChange = { isChecked ->
                    viewModel.updateSetting("dark_theme", isChecked)
                }
            )


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.showLogoutDialog(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showLogoutDialog(false) },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.showLogoutDialog(false)
                            viewModel.logout(navController)
                        }
                    ) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.showLogoutDialog(false) }
                    ) { Text("Cancel") }
                }
            )
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SettingSwitch(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingRadioGroup(
    title: String,
    options: List<String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == index,
                    onClick = { onOptionSelected(index) }
                )
                Text(option, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
