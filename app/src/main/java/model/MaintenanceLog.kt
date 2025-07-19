package com.example.carcare.data.model

/**
 * Data class for a single maintenance log
 */
data class MaintenanceLog(
    val id: String = "",
    val type: String = "",
    val date: String = "",
    val mileage: Int = 0,                     // Changed from String to Int
    val notes: String = "",
    val reminderType: String = "",           // "time" or "mileage"
    val reminderValue: String = "",          // e.g., "5000" km or "6" months
    val vehicleId: String = ""               // New field: links to associated vehicle
)
