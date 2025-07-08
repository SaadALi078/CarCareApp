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

    // 👇 Manually create your repository (no constructor injection)
    private val repository = VehicleRepository()

    private val _state = MutableStateFlow(VehiclesState())
    val state: StateFlow<VehiclesState> = _state.asStateFlow()

    var showAddVehicleDialog by mutableStateOf(false)
    var editingVehicle: Vehicle? by mutableStateOf(null)

    fun loadVehicles() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val vehicles = repository.getVehicles()
                Log.d("VehicleCheck", "📦 Vehicles loaded: $vehicles") // 👈 Add this
                _state.update { it.copy(isLoading = false, vehicles = vehicles) }
            } catch (e: Exception) {
                Log.e("VehicleCheck", "❌ Failed to load", e)
                _state.update {
                    it.copy(isLoading = false, error = "Failed to load vehicles: ${e.message}")
                }
            }
        }
    }

    fun addVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val updatedVehicle = vehicle.copy(userId = currentUserId)
                repository.addVehicle(updatedVehicle)
                loadVehicles()
            } catch (_: Exception) {}
        }
    }




    fun updateVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            try {
                repository.updateVehicle(vehicle)
                loadVehicles()
            } catch (_: Exception) {}
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                repository.deleteVehicle(vehicleId)
                loadVehicles()
            } catch (_: Exception) {}
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
