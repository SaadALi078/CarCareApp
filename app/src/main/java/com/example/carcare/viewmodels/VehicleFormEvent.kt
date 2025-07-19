package com.carcare.viewmodel.vehicle

sealed class VehicleFormEvent {
    data class ShowToast(val message: String) : VehicleFormEvent()
    object NavigateToDashboard : VehicleFormEvent()
}
