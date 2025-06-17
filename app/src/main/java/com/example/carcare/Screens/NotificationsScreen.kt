package com.example.carcare.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import com.example.carcare.navigation.Screen

@Composable
fun NotificationsScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("🚨 Emergency Help Screen – Coming Soon", color = Color.Red)
        }
    }
}
