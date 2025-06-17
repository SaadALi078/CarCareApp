package com.example.carcare.screens

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen

@Composable
fun MaintenanceLogScreenUI() {

    // Handle back press
    BackHandler {
        Router.navigateTo(Screen.MaintenanceScreen)
    }

    // Temporary UI
    Text(text = "Maintenance Log Screen", color = Color.Black)
}
