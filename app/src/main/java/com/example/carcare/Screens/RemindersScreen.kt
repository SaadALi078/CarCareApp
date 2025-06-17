package com.example.carcare.Screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.carcare.navigation.BackHandler
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen

@Composable
fun RemindersScreen()
{

    // 🔙 Handle back press to return to HomeScreen
    BackHandler {
        Router.navigateTo(Screen.HomeScreen)
    }
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Text("⏰ Reminders Screen – Coming Soon")
    }
}