package com.example.carcare.viewmodels

import com.google.firebase.Timestamp

data class FuelLog(
    val id: String = "",
    val vehicleId: String = "",
    val amount: Double = 0.0,
    val cost: Double = 0.0,
    val date: String = "",
    val odometer: Int = 0,
    val notes: String = "",
    val timestamp: Timestamp = Timestamp.now()
)