package com.example.carcare.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.carcare.Screens.AccentMagenta
import com.example.carcare.Screens.LightPurple
import com.example.carcare.Screens.PrimaryPurple


@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 10000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(LightPurple, PrimaryPurple, AccentMagenta),
                    start = androidx.compose.ui.geometry.Offset(offset - 1000, 0f),
                    end = androidx.compose.ui.geometry.Offset(offset, 1000f)
                )
            )
    ) {
        content()
    }
}