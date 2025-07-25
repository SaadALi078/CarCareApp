package com.example.carcare.viewmodels

import com.example.carcare.data.OverpassApiService
import com.example.carcare.data.model.PoiCategory


import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.carcare.data.model.Workshop

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Updated UI State to include POI categories and manage the screen's entire state.
 */
data class EmergencyUiState(
    val isLoading: Boolean = false,
    val locationPermissionGranted: Boolean = false,
    val userLocation: Location? = null,
    val error: String? = null,
    val availableCategories: List<PoiCategory> = listOf(
        PoiCategory.Workshop,
        PoiCategory.AutoParts,
        PoiCategory.CarWash,
        PoiCategory.FuelStation
    ),
    val selectedCategory: PoiCategory = PoiCategory.Workshop, // Default category
    val nearbyPois: List<Workshop> = emptyList(),
    val selectedPoi: Workshop? = null
)

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val overpassApiService: OverpassApiService // Hilt injects our API service
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Handles the result of the location permission request.
     */
    fun onPermissionResult(isGranted: Boolean) {
        _uiState.update { it.copy(locationPermissionGranted = isGranted) }
        if (isGranted) {
            findNearbyPois() // Start search if permission is granted
        } else {
            _uiState.update { it.copy(error = "Location permission is required to find nearby places.") }
        }
    }

    /**
     * Handles the selection of a new POI category from the UI.
     */
    fun onCategorySelected(category: PoiCategory) {
        if (category == _uiState.value.selectedCategory) return // Avoid reloading if same category is clicked
        _uiState.update { it.copy(selectedCategory = category, nearbyPois = emptyList(), selectedPoi = null) }
        findNearbyPois()
    }

    /**
     * The main function to find POIs based on the current location and selected category.
     */
    @SuppressLint("MissingPermission")
    fun findNearbyPois() {
        viewModelScope.launch {
            if (!_uiState.value.locationPermissionGranted) {
                _uiState.update { it.copy(error = "Location permission has not been granted.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null, selectedPoi = null) }
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).await()
                _uiState.update { it.copy(userLocation = location) }
                fetchPoisFromApi(location, _uiState.value.selectedCategory)

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to get user location. Please ensure GPS is enabled.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun setSelectedPoi(poi: Workshop) {
        _uiState.update { it.copy(selectedPoi = poi) }
    }

    fun clearSelectedPoi() {
        _uiState.update { it.copy(selectedPoi = null) }
    }

    /**
     * Constructs a query and fetches data from the Overpass API.
     */
    private suspend fun fetchPoisFromApi(userLocation: Location, category: PoiCategory) {
        val (key, value) = category.osmQueryTag
        val searchRadiusMeters = 15000 // 15km

        val query = """
            [out:json][timeout:25];
            (
              node["$key"="$value"](around:$searchRadiusMeters,${userLocation.latitude},${userLocation.longitude});
              way["$key"="$value"](around:$searchRadiusMeters,${userLocation.latitude},${userLocation.longitude});
              relation["$key"="$value"](around:$searchRadiusMeters,${userLocation.latitude},${userLocation.longitude});
            );
            out center;
        """.trimIndent()

        try {
            val response = overpassApiService.searchNearby(query)
            val pois = response.elements.mapNotNull { element ->
                element.tags["name"]?.let { name ->
                    val poiLocation = Location("poi").apply {
                        latitude = element.lat
                        longitude = element.lon
                    }
                    Workshop(
                        id = element.id.toString(),
                        name = name,
                        latitude = element.lat,
                        longitude = element.lon,
                        phone = element.tags["phone"] ?: "",
                        address = element.tags["addr:full"] ?: element.tags["addr:street"] ?: "Address not available",
                        rating = 0f, // Not provided by API
                        distanceKm = userLocation.distanceTo(poiLocation) / 1000f
                    )
                }
            }.sortedBy { it.distanceKm }

            _uiState.update { it.copy(nearbyPois = pois) }

        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Could not fetch data. Check your internet connection.") }
        }
    }
}