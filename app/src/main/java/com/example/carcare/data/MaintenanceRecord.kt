package com.example.carcare.data

import com.google.firebase.firestore.Exclude

data class MaintenanceRecord(
    @Exclude var id: String = "", // Excluded from Firestore serialization
    val type: String = "",
    val date: String = "",
    val mileage: Int = 0,
    val cost: Double = 0.0,
    val notes: String = "",
    val vehicleId: String = "" // Added to associate records with specific vehicles
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "type" to type,
            "date" to date,
            "mileage" to mileage,
            "cost" to cost,
            "notes" to notes,
            "vehicleId" to vehicleId
        )
    }
}