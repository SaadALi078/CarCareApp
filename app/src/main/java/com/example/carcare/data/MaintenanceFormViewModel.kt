package com.example.carcare.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.MaintenanceRecord
import com.example.carcare.data.repository.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MaintenanceFormViewModel(
    private val repository: MaintenanceRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MaintenanceFormState())
    val state: StateFlow<MaintenanceFormState> = _state.asStateFlow()

    fun loadRecord(recordId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val record = repository.getRecord(recordId) ?: MaintenanceRecord()
                _state.update {
                    it.copy(
                        record = record,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load record: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateRecordField(updatedRecord: MaintenanceRecord) {
        _state.update { it.copy(record = updatedRecord) }
    }

    // MaintenanceFormViewModel.kt (only this)
    fun validateAndSave(context: Context, vehicleId: String)
    {
        val currentRecord = state.value.record.copy(vehicleId = vehicleId)

        // Reset all errors
        _state.update {
            it.copy(
                typeError = null,
                dateError = null,
                mileageError = null,
                costError = null,
                error = null
            )
        }

        // Validate each field
        val isValid = validateFields(currentRecord)
        if (!isValid) return

        // Proceed with saving
        saveRecord(currentRecord, context)
    }

    private fun validateFields(record: MaintenanceRecord): Boolean {
        var isValid = true

        if (record.type.isBlank()) {
            _state.update { it.copy(typeError = "Maintenance type is required") }
            isValid = false
        }

        if (record.date.isBlank()) {
            _state.update { it.copy(dateError = "Date is required") }
            isValid = false
        } else if (!isValidDate(record.date)) {
            _state.update { it.copy(dateError = "Invalid date format (MM/dd/yyyy)") }
            isValid = false
        }

        if (record.mileage <= 0) {
            _state.update { it.copy(mileageError = "Mileage must be positive") }
            isValid = false
        }

        if (record.cost < 0) {
            _state.update { it.copy(costError = "Cost cannot be negative") }
            isValid = false
        }

        return isValid
    }

    private fun isValidDate(dateStr: String): Boolean {
        return try {
            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).apply {
                isLenient = false
            }.parse(dateStr)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun saveRecord(record: MaintenanceRecord, context: Context) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                if (record.id.isEmpty()) {
                    val newId = repository.addRecord(record)
                    _state.update {
                        it.copy(
                            record = record.copy(id = newId),
                            isLoading = false,
                            saveSuccess = true
                        )
                    }
                    Toast.makeText(context, "Maintenance record saved", Toast.LENGTH_SHORT).show()
                } else {
                    repository.updateRecord(record)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            saveSuccess = true
                        )
                    }
                    Toast.makeText(context, "Maintenance record updated", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save record: ${e.message}"
                    )
                }
                Toast.makeText(context, "Failed to save record", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

data class MaintenanceFormState(
    val record: MaintenanceRecord = MaintenanceRecord(),
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val typeError: String? = null,
    val dateError: String? = null,
    val mileageError: String? = null,
    val costError: String? = null
) {
    fun hasValidationErrors(): Boolean {
        return typeError != null || dateError != null || mileageError != null || costError != null
    }
}