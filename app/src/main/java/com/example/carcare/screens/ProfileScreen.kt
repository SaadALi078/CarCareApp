@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.screens
import com.example.carcare.Component.customTextFieldColors

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.carcare.Component.ProfileInfoItem
import com.example.carcare.Component.SettingRadioGroup
import com.example.carcare.Component.SettingSwitch
import com.example.carcare.viewmodels.ProfileViewModel

private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val CardColor = Color(0xFF1E223C)
private val ErrorColor = Color(0xFFFF6B6B)
private val GlassEffect = Brush.linearGradient(
    colors = listOf(PremiumAccent.copy(alpha = 0.1f), Color.Transparent)
)

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val showDialog by viewModel.showLogoutDialog.collectAsState()

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(DeepNavy, MidnightBlue)))
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Profile & Settings",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.8.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier
                        .background(Brush.horizontalGradient(listOf(CeruleanFrost, AquaCyan)))
                        .height(80.dp)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Profile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .shadow(24.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = CardColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GlassEffect, RoundedCornerShape(28.dp))
                            .border(
                                1.dp,
                                Brush.horizontalGradient(
                                    listOf(PremiumAccent.copy(alpha = 0.4f), PremiumAccent.copy(alpha = 0.1f))
                                ),
                                RoundedCornerShape(28.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                "Your Profile",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            if (uiState.isEditing) {
                                OutlinedTextField(
                                    value = viewModel.tempName,
                                    onValueChange = { viewModel.tempName = it },
                                    label = { Text("Name", color = Color.White.copy(alpha = 0.7f)) },
                                    colors = customTextFieldColors(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = viewModel.tempPhone,
                                    onValueChange = { viewModel.tempPhone = it },
                                    label = { Text("Phone", color = Color.White.copy(alpha = 0.7f)) },
                                    colors = customTextFieldColors(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                ProfileInfoItem("Name", uiState.name)
                                ProfileInfoItem("Email", uiState.email)
                                ProfileInfoItem("Phone", uiState.phone)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    if (uiState.isEditing) viewModel.updateProfile()
                                    else viewModel.toggleEditMode()
                                },
                                enabled = !uiState.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(12.dp, RoundedCornerShape(16.dp), clip = true),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(listOf(PremiumAccent, Color(0xFF6A93FF))),
                                            RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.isEditing) Icons.Default.Save else Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            if (uiState.isEditing) "Save Changes" else "Edit Profile",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Settings Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .shadow(24.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = CardColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GlassEffect, RoundedCornerShape(28.dp))
                            .border(
                                1.dp,
                                Brush.horizontalGradient(
                                    listOf(PremiumAccent.copy(alpha = 0.4f), PremiumAccent.copy(alpha = 0.1f))
                                ),
                                RoundedCornerShape(28.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                "App Settings",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            SettingSwitch(
                                title = "Maintenance Reminders",
                                checked = uiState.maintenanceReminders,
                                onCheckedChange = { viewModel.updateSetting("maintenance_reminders", it) },
                                accentColor = PremiumAccent
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            SettingSwitch(
                                title = "Fuel Reminders",
                                checked = uiState.fuelReminders,
                                onCheckedChange = { viewModel.updateSetting("fuel_reminders", it) },
                                accentColor = PremiumAccent
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            SettingRadioGroup(
                                title = "Units",
                                options = listOf("Metric (km, L)", "Imperial (miles, gal)"),
                                selectedOption = if (uiState.unitSystem == "metric") 0 else 1,
                                onOptionSelected = {
                                    val unit = if (it == 0) "metric" else "imperial"
                                    viewModel.updateSetting("unit_system", unit)
                                },
                                accentColor = PremiumAccent
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            SettingSwitch(
                                title = "Dark Theme",
                                checked = uiState.darkTheme,
                                onCheckedChange = {
                                    viewModel.updateSetting("dark_theme", it)
                                },
                                accentColor = PremiumAccent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.showLogoutDialog(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .height(56.dp)
                        .graphicsLayer {
                            scaleX = pulse
                            scaleY = pulse
                        },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(ErrorColor, ErrorColor.copy(alpha = 0.8f))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Logout",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showLogoutDialog(false) },
                title = {
                    Text(
                        "Confirm Logout",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to logout?",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.showLogoutDialog(false)
                            viewModel.logout(navController)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Yes", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.showLogoutDialog(false) },
                        border = BorderStroke(1.dp, PremiumAccent),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PremiumAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = CardColor,
                modifier = Modifier.border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(PremiumAccent.copy(alpha = 0.4f), PremiumAccent.copy(alpha = 0.1f))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
            )
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = PremiumAccent,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}


// DUPLICATE CODE REMOVED FROM HERE