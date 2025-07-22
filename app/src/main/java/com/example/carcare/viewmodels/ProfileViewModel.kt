package com.example.carcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog

    var tempName = ""
    var tempPhone = ""

    init {
        loadProfile()
        loadSettings()
    }

    fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get().addOnSuccessListener { doc ->
            _uiState.update {
                it.copy(
                    name = doc.getString("name") ?: "",
                    email = doc.getString("email") ?: auth.currentUser?.email ?: "",
                    phone = doc.getString("phone") ?: ""
                )
            }
        }
    }

    fun loadSettings() {
        val uid = auth.currentUser?.uid ?: return
        val settingsRef = firestore.collection("settings").document(uid)

        settingsRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                _uiState.update {
                    it.copy(
                        maintenanceReminders = doc.getBoolean("maintenance_reminders") ?: true,
                        fuelReminders = doc.getBoolean("fuel_reminders") ?: true,
                        unitSystem = doc.getString("unit_system") ?: "metric",
                        darkTheme = doc.getBoolean("dark_theme") ?: false
                    )
                }
            } else {
                // Create default settings if not found
                val defaultSettings = mapOf(
                    "maintenance_reminders" to true,
                    "fuel_reminders" to true,
                    "unit_system" to "metric",
                    "dark_theme" to false
                )
                settingsRef.set(defaultSettings)
            }
        }
    }

    fun updateSetting(key: String, value: Any) {
        val uid = auth.currentUser?.uid ?: return
        val settingsRef = firestore.collection("settings").document(uid)

        settingsRef.update(key, value).addOnSuccessListener {
            _uiState.update {
                when (key) {
                    "maintenance_reminders" -> it.copy(maintenanceReminders = value as Boolean)
                    "fuel_reminders" -> it.copy(fuelReminders = value as Boolean)
                    "unit_system" -> it.copy(unitSystem = value as String)
                    "dark_theme" -> it.copy(darkTheme = value as Boolean)
                    else -> it
                }
            }
        }
    }

    fun toggleEditMode() {
        _uiState.value = _uiState.value.copy(isEditing = true)
        tempName = _uiState.value.name
        tempPhone = _uiState.value.phone
    }

    fun updateProfile() {
        val uid = auth.currentUser?.uid ?: return
        _uiState.value = _uiState.value.copy(isLoading = true)

        firestore.collection("users").document(uid)
            .update(mapOf("name" to tempName, "phone" to tempPhone))
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(
                    name = tempName,
                    phone = tempPhone,
                    isEditing = false,
                    isLoading = false
                )
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
    }

    fun logout(navController: androidx.navigation.NavController) {
        auth.signOut()
        navController.navigate("login") {
            popUpTo(0)
        }
    }

    fun showLogoutDialog(show: Boolean) {
        _showLogoutDialog.value = show
    }
}
