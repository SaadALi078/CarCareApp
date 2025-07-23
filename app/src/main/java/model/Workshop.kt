package com.example.carcare.data.model

data class Workshop(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String = "",
    val rating: Float = 0f,
    val distanceKm: Float = 0f
)
