package com.example.carcare.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.MaintenanceRecord
import com.example.carcare.data.MaintenanceRepository
import kotlinx.coroutines.launch

class MaintenanceLogViewModel : ViewModel() {
    var state by mutableStateOf(MaintenanceLogState())
        private set

    private val repository = MaintenanceRepository()

    fun loadRecords(category: String? = null) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val records = repository.getRecordsByCategory(category)
                state = state.copy(
                    records = records,
                    isLoading = false
                )
            } catch (e: Exception) {
                state = state.copy(
                    error = "Failed to load records",
                    isLoading = false
                )
            }
        }
    }
}

data class MaintenanceLogState(
    val records: List<MaintenanceRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)