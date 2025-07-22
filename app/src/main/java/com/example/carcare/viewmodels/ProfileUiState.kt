package com.example.carcare.viewmodels

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val maintenanceReminders: Boolean = true,
    val fuelReminders: Boolean = true,
    val unitSystem: String = "metric",
    val darkTheme: Boolean? = null
)
