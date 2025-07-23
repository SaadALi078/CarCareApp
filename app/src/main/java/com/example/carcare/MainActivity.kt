package com.example.carcare

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.carcare.navigation.AppNavigation
import com.example.carcare.ui.theme.CarCareAppTheme
import com.example.carcare.viewmodels.ThemeViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Firebase init only
        FirebaseApp.initializeApp(this)

        // ✅ Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // ✅ Main App UI
        setContent {
            val navController = rememberNavController()
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val darkTheme by themeViewModel.darkTheme.collectAsState(initial = false)

            CarCareAppTheme(darkTheme = darkTheme) {
                AppNavigation(navController)
            }
        }
    }
}
