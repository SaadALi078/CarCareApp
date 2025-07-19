package com.carcare.viewmodel.vehicle

data class VehicleFormState(
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val plate: String = "",
    val mileage: String = "",
    val isLoading: Boolean = false
)
