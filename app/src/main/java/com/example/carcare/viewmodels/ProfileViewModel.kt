package com.example.carcare.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    var tempName = ""
    var tempPhone = ""

    init {
        loadProfile()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _uiState.update {
                    it.copy(
                        maintenanceReminders = prefs[booleanPreferencesKey("maintenance_reminders")] ?: true,
                        fuelReminders = prefs[booleanPreferencesKey("fuel_reminders")] ?: true,
                        unitSystem = prefs[stringPreferencesKey("unit_system")] ?: "metric",
                        darkTheme = prefs[booleanPreferencesKey("dark_theme")] ?: false
                    )
                }
            }
        }
    }

    fun updateSetting(key: String, value: Any) {
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                when (key) {
                    "maintenance_reminders" -> settings[booleanPreferencesKey(key)] = value as Boolean
                    "fuel_reminders" -> settings[booleanPreferencesKey(key)] = value as Boolean
                    "unit_system" -> settings[stringPreferencesKey(key)] = value as String
                    "dark_theme" -> settings[booleanPreferencesKey(key)] = value as Boolean
                }
            }
        }
    }

    fun loadProfile() {
        val user = auth.currentUser ?: return
        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                _uiState.update {
                    it.copy(
                        name = doc.getString("name") ?: "",
                        email = user.email ?: "",
                        phone = doc.getString("phone") ?: "",
                        isLoading = false
                    )
                }
            }
    }

    fun toggleEditMode() {
        _uiState.update {
            it.copy(isEditing = true)
        }
        tempName = _uiState.value.name
        tempPhone = _uiState.value.phone
    }

    fun updateProfile() {
        val uid = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(isLoading = true) }

        firestore.collection("users").document(uid)
            .update(mapOf("name" to tempName, "phone" to tempPhone))
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        name = tempName,
                        phone = tempPhone,
                        isEditing = false,
                        isLoading = false
                    )
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false) }
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

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = true,
    val maintenanceReminders: Boolean = true,
    val fuelReminders: Boolean = true,
    val unitSystem: String = "metric",
    val darkTheme: Boolean = false
)
