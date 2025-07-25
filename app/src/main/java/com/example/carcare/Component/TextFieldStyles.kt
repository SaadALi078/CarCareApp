package com.example.carcare.Component

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PremiumAccent = Color(0xFF4A7BFF)
val ErrorColor = Color(0xFFFF6B6B)

@Composable
fun customTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PremiumAccent.copy(alpha = 0.7f),
        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
        cursorColor = PremiumAccent,
        focusedLabelColor = PremiumAccent.copy(alpha = 0.7f),
        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
        errorBorderColor = ErrorColor,
        errorLabelColor = ErrorColor,
        disabledTextColor = Color.Gray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        disabledBorderColor = Color.DarkGray,
        disabledLabelColor = Color.Gray
    )
}
