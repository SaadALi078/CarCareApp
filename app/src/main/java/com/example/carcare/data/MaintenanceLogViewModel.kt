package com.example.carcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.MaintenanceRecord
import com.example.carcare.data.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaintenanceLogViewModel : ViewModel() {
    private val _state = MutableStateFlow(MaintenanceLogState())
    val state: StateFlow<MaintenanceLogState> = _state.asStateFlow()

    private val repository = MaintenanceRepository() // Make sure to implement this

    fun loadRecords(category: String? = null) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val records = if (category != null) {
                    repository.getRecordsByCategory(category)
                } else {
                    repository.getAllRecords()
                }

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