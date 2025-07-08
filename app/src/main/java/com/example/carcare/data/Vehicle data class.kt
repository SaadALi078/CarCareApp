package com.example.carcare.data

data class Vehicle(
    val id: String = "", // ✅ MUST HAVE THIS FIELD
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val year: Int? = null,
    val licensePlate: String = "",
    val currentMileage: Int = 0,
    val maintenanceCount: Int = 0,
    val userId: String = ""// Optional
)
