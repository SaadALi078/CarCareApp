package com.example.carcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.model.MaintenanceLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaintenanceViewModel : ViewModel() {

    private val _logs = MutableStateFlow<List<MaintenanceLog>>(emptyList())
    val logs: StateFlow<List<MaintenanceLog>> = _logs

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private var listenerRegistration: ListenerRegistration? = null

    fun loadLogs(vehicleId: String) {
        if (uid == null) return

        // Remove previous listener to prevent leaks
        listenerRegistration?.remove()

        listenerRegistration = db.collection("users")
            .document(uid)
            .collection("vehicles")
            .document(vehicleId)
            .collection("maintenance")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener

                val logs = snapshots.documents.mapNotNull { doc ->
                    try {
                        MaintenanceLog(
                            id = doc.id,
                            type = doc.getString("type") ?: "",
                            date = doc.getString("date") ?: "",
                            mileage = (doc.getLong("mileage") ?: 0L).toInt(),
                            notes = doc.getString("notes") ?: "",
                            reminderType = doc.getString("reminderType") ?: "",
                            reminderValue = doc.getString("reminderValue") ?: "",
                            vehicleId = doc.getString("vehicleId") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                _logs.value = logs
            }
    }

    fun deleteLog(vehicleId: String, logId: String, onSuccess: () -> Unit = {}) {
        if (uid == null) return

        db.collection("users")
            .document(uid)
            .collection("vehicles")
            .document(vehicleId)
            .collection("maintenance")
            .document(logId)
            .delete()
            .addOnSuccessListener { onSuccess() }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
