package com.example.carcare.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Note: This file should be located at: .../app/src/main/java/com/example/carcare/viewmodels/VehicleDashboardViewModel.kt

data class VehicleDashboardState(
    val vehicles: List<VehicleItem> = emptyList(),
    val upcomingReminder: MaintenanceReminderItem? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class VehicleItem(
    val id: String = "",
    val make: String = "",
    val model: String = "",
    val mileage: String = ""
)

data class MaintenanceReminderItem(
    val vehicleId: String,
    val logId: String,
    val type: String,
    val dueDate: String,
    val date: String = "",
)

class VehicleDashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(VehicleDashboardState())
    val state: StateFlow<VehicleDashboardState> = _state

    private val _fuelLogs = mutableStateListOf<FuelLog>()
    val fuelLogs: List<FuelLog> get() = _fuelLogs

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val db = FirebaseFirestore.getInstance()

    init {
        listenToVehicles()
    }

    private fun listenToVehicles() {
        if (uid.isEmpty()) return
        _state.value = _state.value.copy(isLoading = true)

        db.collection("users")
            .document(uid)
            .collection("vehicles")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error?.message ?: "Unknown error"
                    )
                    return@addSnapshotListener
                }

                val vehicles = snapshot.documents.mapNotNull { doc ->
                    val make = doc.getString("make") ?: return@mapNotNull null
                    val model = doc.getString("model") ?: ""
                    val mileage = doc.getString("mileage") ?: ""
                    VehicleItem(
                        id = doc.id,
                        make = make,
                        model = model,
                        mileage = mileage
                    )
                }

                _state.value = _state.value.copy(vehicles = vehicles, isLoading = false)
                fetchUpcomingReminder(vehicles)
                // Also load fuel logs for the first vehicle if available
                vehicles.firstOrNull()?.id?.let { loadFuelLogs(it) }
            }
    }

    fun loadFuelLogs(vehicleId: String) {
        if (uid.isEmpty()) return

        viewModelScope.launch {
            db.collection("users")
                .document(uid)
                .collection("vehicles")
                .document(vehicleId)
                .collection("fuel_logs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val newLogs = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(FuelLog::class.java)?.copy(id = doc.id)
                    }
                    _fuelLogs.clear()
                    _fuelLogs.addAll(newLogs)
                }
                .addOnFailureListener {
                    // Handle error, e.g., update state with an error message
                    _state.value = _state.value.copy(error = it.message)
                }
        }
    }
    fun deleteFuelLog(vehicleId: String, logId: String) {
        if (uid.isEmpty()) return

        db.collection("users")
            .document(uid)
            .collection("vehicles")
            .document(vehicleId)
            .collection("fuel_logs")
            .document(logId)
            .delete()
            .addOnSuccessListener {
                _fuelLogs.removeAll { it.id == logId }
            }
            .addOnFailureListener {
                _state.value = _state.value.copy(error = it.message)
            }
    }

    fun addFuelLog(
        vehicleId: String,
        amount: Double,
        cost: Double,
        date: String,
        odometer: Int,
        notes: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (uid.isEmpty()) {
            onFailure(Exception("User not logged in."))
            return
        }

        viewModelScope.launch {
            try {
                val data = hashMapOf(
                    "amount" to amount,
                    "cost" to cost,
                    "date" to date,
                    "odometer" to odometer,
                    "notes" to notes,
                    "timestamp" to Timestamp.now()
                )

                db.collection("users")
                    .document(uid)
                    .collection("vehicles")
                    .document(vehicleId)
                    .collection("fuel_logs")
                    .add(data)
                    .addOnSuccessListener { docRef ->
                        // Re-load logs to ensure sorted order
                        loadFuelLogs(vehicleId)
                        onSuccess()
                    }
                    .addOnFailureListener(onFailure)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    private fun fetchUpcomingReminder(vehicles: List<VehicleItem>) {
        if (uid.isEmpty()) return

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val now = Date()
        var soonest: MaintenanceReminderItem? = null

        vehicles.forEach { vehicle ->
            db.collection("users")
                .document(uid)
                .collection("vehicles")
                .document(vehicle.id)
                .collection("maintenance")
                .addSnapshotListener { logs, _ ->
                    logs?.documents?.forEach { doc ->
                        val type = doc.getString("type") ?: return@forEach
                        val dateStr = doc.getString("date") ?: return@forEach
                        val reminderType = doc.getString("reminderType") ?: return@forEach
                        val reminderValue = doc.getString("reminderValue") ?: return@forEach

                        if (reminderType.equals("time", ignoreCase = true)) {
                            try {
                                val baseDate = sdf.parse(dateStr) ?: return@forEach
                                val days = reminderValue.toIntOrNull() ?: return@forEach
                                val dueDate = Calendar.getInstance().apply {
                                    time = baseDate
                                    add(Calendar.DAY_OF_YEAR, days)
                                }.time

                                if (dueDate.after(now)) {
                                    val currentSoonestDueDate = soonest?.let { sdf.parse(it.dueDate) }
                                    if (currentSoonestDueDate == null || dueDate.before(currentSoonestDueDate)) {
                                        soonest = MaintenanceReminderItem(
                                            vehicleId = vehicle.id,
                                            logId = doc.id,
                                            type = type,
                                            dueDate = sdf.format(dueDate)
                                        )
                                        _state.value = _state.value.copy(upcomingReminder = soonest)
                                    }
                                }
                            } catch (_: Exception) {
                                // Ignore parsing errors
                            }
                        }
                    }
                }
        }
    }
}