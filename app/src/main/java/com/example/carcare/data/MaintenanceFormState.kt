package com.example.carcare.data

import com.example.carcare.data.MaintenanceRecord

data class MaintenanceFormState(
    val record: MaintenanceRecord = MaintenanceRecord(),
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val typeError: String? = null,
    val dateError: String? = null,
    val mileageError: String? = null,
    val costError: String? = null
)
