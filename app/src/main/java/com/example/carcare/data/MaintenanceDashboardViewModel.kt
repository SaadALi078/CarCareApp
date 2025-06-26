package com.example.carcare.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaintenanceDashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(MaintenanceDashboardState())
    val state: StateFlow<MaintenanceDashboardState> = _state.asStateFlow()

    // Track the current vehicle ID
    private var _currentVehicleId: String = ""
    val currentVehicleId: String get() = _currentVehicleId

    fun setVehicleId(vehicleId: String) {
        _currentVehicleId = vehicleId
        loadData()
    }

    init {
        // Don't load data until vehicle ID is set
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1200) // Simulate API call

            _state.update {
                it.copy(
                    isLoading = false,
                    upcomingCount = 3,
                    overdueCount = 1,
                    categories = listOf(
                        MaintenanceCategory("Oil Change", R.drawable.ic_oil),
                        MaintenanceCategory("Tire Rotation", R.drawable.ic_tire),
                        MaintenanceCategory("Brake Check", R.drawable.ic_brake),
                        MaintenanceCategory("Filter Replacement", R.drawable.ic_filter),
                        MaintenanceCategory("Fluid Check", R.drawable.ic_fluid),
                        MaintenanceCategory("Battery Check", R.drawable.ic_battery)
                    ),
                    recentMaintenance = listOf(
                        MaintenanceRecord(
                            id = "1",
                            type = "Oil Change",
                            date = "May 12, 2025",
                            mileage = 12500,
                            cost = 59.99
                        ),
                        MaintenanceRecord(
                            id = "2",
                            type = "Tire Rotation",
                            date = "Apr 28, 2025",
                            mileage = 12000,
                            cost = 29.99
                        ),
                        MaintenanceRecord(
                            id = "3",
                            type = "Brake Inspection",
                            date = "Apr 15, 2025",
                            mileage = 11500,
                            cost = 0.0
                        )
                    )
                )
            }
        }
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