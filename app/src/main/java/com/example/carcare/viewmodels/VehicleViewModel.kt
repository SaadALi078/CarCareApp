package com.example.carcare.viewmodel.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carcare.repository.VehicleRepository
import com.carcare.viewmodel.vehicle.VehicleFormEvent
import com.carcare.viewmodel.vehicle.VehicleFormState

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VehicleViewModel(
    private val repository: VehicleRepository = VehicleRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleFormState())
    val state: StateFlow<VehicleFormState> = _state

    private val _event = MutableSharedFlow<VehicleFormEvent>()
    val event: SharedFlow<VehicleFormEvent> = _event

    fun onFieldChange(field: String, value: String) {
        _state.update {
            when (field) {
                "make" -> it.copy(make = value)
                "model" -> it.copy(model = value)
                "year" -> it.copy(year = value)
                "plate" -> it.copy(plate = value)
                "mileage" -> it.copy(mileage = value)
                else -> it
            }
        }
    }

    fun saveVehicle() {
        val current = _state.value
        if (current.make.isBlank() || current.model.isBlank() || current.year.isBlank()
            || current.plate.isBlank() || current.mileage.isBlank()
        ) {
            viewModelScope.launch {
                _event.emit(VehicleFormEvent.ShowToast("Please fill in all fields"))
            }
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = repository.saveVehicle(
                current.make,
                current.model,
                current.year,
                current.plate,
                current.mileage
            )

            _state.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = {
                    _event.emit(VehicleFormEvent.NavigateToDashboard)
                },
                onFailure = {
                    _event.emit(VehicleFormEvent.ShowToast("Failed: ${it.message}"))
                }
            )
        }
    }
}
