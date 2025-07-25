package com.example.carcare.data.model

// In your Workshop.kt or relevant data model file
data class Workshop(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    val rating: Float,
    val distanceKm: Float,
    val address: String // <-- Add this line
)