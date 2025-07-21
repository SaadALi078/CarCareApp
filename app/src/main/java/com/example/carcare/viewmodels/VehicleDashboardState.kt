package com.example.carcare.viewmodels.vehicle

data class VehicleDashboardState(
    val vehicles: List<VehicleItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,


)

data class VehicleItem(
    val id: String = "",
    val make: String = "",
    val model: String = "",
    val mileage: String = ""
)
