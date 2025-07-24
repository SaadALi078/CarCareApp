package com.example.carcare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Premium Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF007AFF),           // Electric Blue
    primaryContainer = Color(0xFF0A1128),  // Deep Sapphire
    secondary = Color(0xFFFF0101),         // Porsche Red
    secondaryContainer = Color(0xFFC0C0C0),// Titanium Silver
    background = Color(0xFF121212),        // Space Black
    surface = Color(0xFF1A1A1A),           // Onyx
    onPrimary = Color(0xFFFFFFFF),         // Arctic White
    onSecondary = Color(0xFFFFFFFF),       // Arctic White
    onBackground = Color(0xFFE0E0E0),      // Platinum
    onSurface = Color(0xFFE0E0E0),         // Platinum
    error = Color(0xFFFF0101)              // Porsche Red
)

// Light Theme (for consistency)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0A1128),            // Deep Sapphire
    primaryContainer = Color(0xFF007AFF),   // Electric Blue
    secondary = Color(0xFFFF0101),          // Porsche Red
    secondaryContainer = Color(0xFFF0F0F0), // Light Silver
    background = Color(0xFFF5F5F7),         // Light Gray
    surface = Color(0xFFFFFFFF),            // Pure White
    onPrimary = Color(0xFFFFFFFF),          // Arctic White
    onSecondary = Color(0xFFFFFFFF),        // Arctic White
    onBackground = Color(0xFF121212),       // Space Black
    onSurface = Color(0xFF1E1E1E),          // Carbon Fiber Gray
    error = Color(0xFFD32F2F)               // Error Red
)
val PremiumDarkColorPalette = darkColorScheme(
    primary = Color(0xFF6C56E3),
    primaryContainer = Color(0xFF0A1128),
    secondary = Color(0xFFFF7BAC),
    secondaryContainer = Color(0xFF2A2D3E),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E2A),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFC5C5D0)
)


@Composable
fun CarCareAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CarCareTypography,
        content = content
    )
}

// Optional Custom Colors
val PremiumAccent = Color(0xFF00C853)         // Emerald (Success)
val PremiumWarning = Color(0xFFFFC107)        // Amber (Warning)
val PremiumSurfaceVariant = Color(0xFF2A2A2A) // Dark surface variant
