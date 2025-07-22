package com.example.carcare.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore

// Define Context extension for dataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
