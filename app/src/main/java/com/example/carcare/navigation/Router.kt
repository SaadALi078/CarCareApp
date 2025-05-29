package com.example.carcare.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object Signup : Screen()
    object TermsAndCondtionsScreen : Screen()
    object LoginScreen:Screen()
    object HomeScreen:Screen()
}

object Router {
    val currentScreen: MutableState<Screen> = mutableStateOf(Screen.Signup)
    fun navigateTo(destinaiton: Screen){
        currentScreen.value=destinaiton
    }
}
