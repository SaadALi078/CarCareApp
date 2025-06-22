package com.example.carcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.MaintenanceRecord
import com.example.carcare.data.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaintenanceFormViewModel : ViewModel() {

    private val repository = MaintenanceRepository()

    private val _state = MutableStateFlow(MaintenanceFormState())
    val state: StateFlow<MaintenanceFormState> = _state

    fun loadRecord(recordId: String) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            val record = repository.getRecord(recordId)
            _state.value = _state.value.copy(record = record, isLoading = false)
        }
    }

    fun updateRecordField(updatedRecord: MaintenanceRecord) {
        _state.value = _state.value.copy(record = updatedRecord)
    }

    fun saveRecord(record: MaintenanceRecord) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                if (record.id.isEmpty()) {
                    repository.addRecord(record)
                } else {
                    repository.updateRecord(record)
                }
                _state.value = _state.value.copy(isLoading = false, saveSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Failed to save record")
            }
        }
    }
}
