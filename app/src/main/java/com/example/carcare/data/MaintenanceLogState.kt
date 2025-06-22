// com.example.carcare.data.MaintenanceLogState.kt
package com.example.carcare.data

data class MaintenanceLogState(
    val isLoading: Boolean = false,
    val records: List<MaintenanceRecord> = emptyList(),
    val error: String? = null
)
