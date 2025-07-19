package com.example.carcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class VehicleDashboardState(
    val vehicles: List<VehicleItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class VehicleItem(
    val id: String = "",
    val make: String = "",
    val model: String = "",
    val mileage: String = ""
)

class VehicleDashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(VehicleDashboardState(isLoading = true))
    val state: StateFlow<VehicleDashboardState> = _state

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("vehicles")
            .get()
            .addOnSuccessListener { snapshot ->
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
                _state.value = VehicleDashboardState(vehicles = vehicles)
            }
            .addOnFailureListener {
                _state.value = VehicleDashboardState(error = "Failed to load vehicles")
            }
    }
}
