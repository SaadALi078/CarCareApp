package com.example.carcare.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.repository.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VehiclesViewModel : ViewModel() {
    private val repository = VehicleRepository()
    private val _state = MutableStateFlow(VehiclesState())
    val state: StateFlow<VehiclesState> = _state.asStateFlow()

    var showAddVehicleDialog by mutableStateOf(false)
    var editingVehicle: Vehicle? by mutableStateOf(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadVehicles() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val vehicles = repository.getVehicles()
                Log.d("VehicleCheck", "📦 Vehicles loaded: $vehicles")
                _state.update { it.copy(isLoading = false, vehicles = vehicles, error = null) }
            } catch (e: Exception) {
                Log.e("VehicleCheck", "❌ Failed to load", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load vehicles: ${e.message}"
                    )
                }
            }
        }
    }

    fun addVehicle(vehicle: Vehicle, onSuccess: (String) -> Unit) {
        // Validate vehicle data
        if (vehicle.name.isBlank()) {
            errorMessage = "Vehicle name is required"
            return
        }
        if (vehicle.year == null || vehicle.year!! < 1900 || vehicle.year!! > java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)) {
            errorMessage = "Invalid year"
            return
        }
        if (vehicle.currentMileage < 0) {
            errorMessage = "Mileage must be positive"
            return
        }

        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val updatedVehicle = vehicle.copy(userId = currentUserId)
                repository.addVehicle(updatedVehicle)
                loadVehicles()
                onSuccess(updatedVehicle.id)
                errorMessage = null
            } catch (e: Exception) {
                Log.e("VehicleCheck", "❌ Failed to add vehicle", e)
                errorMessage = "Failed to add vehicle: ${e.message}"
            }
        }
    }

    fun updateVehicle(vehicle: Vehicle) {
        // Validate vehicle data
        if (vehicle.name.isBlank()) {
            errorMessage = "Vehicle name is required"
            return
        }
        if (vehicle.year == null || vehicle.year!! < 1900 || vehicle.year!! > java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)) {
            errorMessage = "Invalid year"
            return
        }
        if (vehicle.currentMileage < 0) {
            errorMessage = "Mileage must be positive"
            return
        }

        viewModelScope.launch {
            try {
                repository.updateVehicle(vehicle)
                loadVehicles()
                errorMessage = null
            } catch (e: Exception) {
                Log.e("VehicleCheck", "❌ Failed to update vehicle", e)
                errorMessage = "Failed to update vehicle: ${e.message}"
            }
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                repository.deleteVehicle(vehicleId)
                loadVehicles()
                errorMessage = null
            } catch (e: Exception) {
                Log.e("VehicleCheck", "❌ Failed to delete vehicle", e)
                errorMessage = "Failed to delete vehicle: ${e.message}"
            }
        }
    }

    fun startEditing(vehicle: Vehicle) {
        editingVehicle = vehicle
        showAddVehicleDialog = true
    }
}

data class VehiclesState(
    val isLoading: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val error: String? = null
)