package com.example.carcare.viewmodels

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.themeDataStore by preferencesDataStore(name = "theme_settings")

@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    // Exposed theme state
    val darkTheme: StateFlow<Boolean> = context.themeDataStore.data
        .map { prefs -> prefs[DARK_THEME_KEY] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Toggle and save to DataStore
    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            context.themeDataStore.edit { prefs ->
                prefs[DARK_THEME_KEY] = enabled
            }
        }
    }
}
