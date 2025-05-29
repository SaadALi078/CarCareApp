package com.example.carcare.navigation

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.*

private class ComposableBackNavigationHandler(
    onBackPressed: () -> Unit
) : OnBackPressedCallback(true) {

    var backPressed: () -> Unit = onBackPressed

    override fun handleOnBackPressed() {
        backPressed()
    }
}

@Composable
fun BackHandler(
    enabled: Boolean = true,
    onBackPressed: () -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher ?: return
    val handler = remember {
        ComposableBackNavigationHandler(onBackPressed)
    }

    // Update handler state
    LaunchedEffect(enabled, onBackPressed) {
        handler.isEnabled = enabled
        handler.backPressed = onBackPressed
    }

    DisposableEffect(dispatcher) {
        dispatcher.addCallback(handler)
        onDispose {
            handler.remove()
        }
    }
}
