package com.example.carcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.MaintenanceRecord
import com.example.carcare.data.repository.MaintenanceRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaintenanceLogViewModel(
    private val repository: MaintenanceRepository,
    private val vehicleId: String
) : ViewModel() {
    private val _state = MutableStateFlow(MaintenanceLogState())
    val state: StateFlow<MaintenanceLogState> = _state.asStateFlow()

    fun loadRecords(category: String? = null) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val records = repository.getRecordsByVehicleAndCategory(vehicleId, category)

                _state.update {
                    it.copy(
                        records = records,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load records: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}

data class MaintenanceLogState(
    val records: List<MaintenanceRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)