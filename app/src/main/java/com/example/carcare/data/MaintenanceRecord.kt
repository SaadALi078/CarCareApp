package com.example.carcare.data

data class MaintenanceRecord(
    val id: String = "",
    val vehicleId: String = "",
    val type: String = "",
    val description: String = "",
    val date: String = "",
    val cost: Double = 0.0,
    val mileage: Int = 0,
    val category: String = "",
    val notes: String = ""
)
