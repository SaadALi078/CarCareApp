package com.example.carcare.viewmodels

import com.example.carcare.data.MaintenanceRecord

data class MaintenanceFormState(
    val isLoading: Boolean = false,
    val record: MaintenanceRecord = MaintenanceRecord(),
    val error: String? = null,
    val saveSuccess: Boolean = false
)
