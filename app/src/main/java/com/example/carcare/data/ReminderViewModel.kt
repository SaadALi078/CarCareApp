// viewmodels/ReminderViewModel.kt
package com.example.carcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.Reminder
import com.example.carcare.data.ReminderRepository
import com.example.carcare.data.ReminderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ReminderState())
    val state: StateFlow<ReminderState> = _state.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _currentReminder = MutableStateFlow<Reminder?>(null)
    val currentReminder: StateFlow<Reminder?> = _currentReminder.asStateFlow()

    fun loadReminders(userId: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val reminders = repository.getUserReminders(userId)
                _state.update {
                    it.copy(
                        reminders = reminders,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load reminders: ${e.message}"
                    )
                }
            }
        }
    }

    fun showAddReminderDialog() {
        _currentReminder.value = Reminder()
        _showAddDialog.value = true
    }
    fun setCurrentReminder(reminder: Reminder) {
        _currentReminder.value = reminder
    }

    fun showEditReminderDialog(reminder: Reminder) {
        _currentReminder.value = reminder
        _showAddDialog.value = true
    }

    fun hideAddDialog() {
        _showAddDialog.value = false
        _currentReminder.value = null
    }

    fun showDeleteConfirmation(reminder: Reminder) {
        _currentReminder.value = reminder
        _showDeleteDialog.value = true
    }

    fun hideDeleteDialog() {
        _showDeleteDialog.value = false
        _currentReminder.value = null
    }

    fun saveReminder(userId: String, vehicleId: String, vehicleName: String) {
        val current = _currentReminder.value ?: return
        val reminder = current.copy(
            userId = userId,
            vehicleId = vehicleId,
            vehicleName = vehicleName
        )

        viewModelScope.launch {
            try {
                if (reminder.id.isEmpty()) {
                    repository.addReminder(reminder)
                } else {
                    repository.updateReminder(reminder)
                }
                loadReminders(userId)
                hideAddDialog()
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to save reminder: ${e.message}")
                }
            }
        }
    }

    fun deleteReminder() {
        val reminder = _currentReminder.value ?: return
        viewModelScope.launch {
            try {
                repository.deleteReminder(reminder.id)
                loadReminders(reminder.userId)
                hideDeleteDialog()
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to delete reminder: ${e.message}")
                }
            }
        }
    }

    fun markAsComplete(reminder: Reminder) {
        viewModelScope.launch {
            try {
                repository.markAsComplete(reminder.id)
                loadReminders(reminder.userId)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to mark as complete: ${e.message}")
                }
            }
        }
    }
}

data class ReminderState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)