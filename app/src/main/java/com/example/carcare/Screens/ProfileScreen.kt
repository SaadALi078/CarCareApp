@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.carcare.Screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Emergency Help") }) }
    ) { padding ->
        Text(
            "Emergency help will be available here.",
            modifier = Modifier.padding(padding).padding(16.dp)
        )
    }
}

