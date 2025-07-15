package com.example.carcare.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.R
import com.example.carcare.data.repository.MaintenanceRepository
import com.example.carcare.data.repository.ReminderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit


class MaintenanceDashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(MaintenanceDashboardState())
    val state: StateFlow<MaintenanceDashboardState> = _state.asStateFlow()

    // Track the current vehicle
    private var _currentVehicle: Vehicle? by mutableStateOf(null)

    fun setVehicle(vehicle: Vehicle) {
        _currentVehicle = vehicle
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1200) // Simulate API call

            val vehicle = _currentVehicle ?: run {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            val currentDate = Date()
            val currentMileage = vehicle.currentMileage

            // Get reminders and maintenance records
            val reminders = ReminderRepository().getVehicleReminders(vehicle.id)
            val maintenanceRecords = MaintenanceRepository().getRecentMaintenance(vehicle.id, 3)

            // Calculate counts
            val overdueCount = reminders.count { reminder ->
                val dueDate = reminder.dueDate.toDate()
                val isOverdueByDate = dueDate.before(currentDate)
                val isOverdueByMileage = reminder.odometerThreshold > 0 &&
                        currentMileage >= reminder.odometerThreshold

                isOverdueByDate || isOverdueByMileage
            }

            val upcomingCount = reminders.count { reminder ->
                val dueDate = reminder.dueDate.toDate()
                val isUpcoming = !dueDate.before(currentDate) &&
                        daysUntil(dueDate) <= 7
                val isNotOverdue = !(reminder.odometerThreshold > 0 &&
                        currentMileage >= reminder.odometerThreshold)

                isUpcoming && isNotOverdue
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    upcomingCount = upcomingCount,
                    overdueCount = overdueCount,
                    categories = listOf(
                        MaintenanceCategory("Oil Change", R.drawable.ic_oil),
                        MaintenanceCategory("Tire Rotation", R.drawable.ic_tire),
                        MaintenanceCategory("Brake Check", R.drawable.ic_brake),
                        MaintenanceCategory("Filter Replacement", R.drawable.ic_filter),
                        MaintenanceCategory("Fluid Check", R.drawable.ic_fluid),
                        MaintenanceCategory("Battery Check", R.drawable.ic_battery)
                    ),
                    recentMaintenance = maintenanceRecords
                )
            }
        }
    }

    private fun daysUntil(date: Date): Long {
        val diff = date.time - Date().time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun selectCategory(category: String) {
        // Save for pre-selection in log screen
    }
}

data class MaintenanceDashboardState(
    val isLoading: Boolean = true,
    val upcomingCount: Int = 0,
    val overdueCount: Int = 0,
    val categories: List<MaintenanceCategory> = emptyList(),
    val recentMaintenance: List<MaintenanceRecord> = emptyList()
)

data class MaintenanceCategory(
    val name: String,
    val iconRes: Int
)