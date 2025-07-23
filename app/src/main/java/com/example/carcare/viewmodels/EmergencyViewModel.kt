package com.example.carcare.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.carcare.data.model.Workshop
import com.google.android.gms.location.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    private val _nearbyWorkshops = MutableStateFlow<List<Workshop>>(emptyList())
    val nearbyWorkshops: StateFlow<List<Workshop>> = _nearbyWorkshops

    private val _selectedWorkshop = MutableStateFlow<Workshop?>(null)
    val selectedWorkshop: StateFlow<Workshop?> = _selectedWorkshop

    fun updateLocationPermission(granted: Boolean) {
        _locationPermissionGranted.value = granted
        if (granted) {
            fetchUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _userLocation.value = location
                loadNearbyWorkshops(location)
            }
        }
    }

    private fun loadNearbyWorkshops(location: Location) {
        val lat = location.latitude
        val lng = location.longitude

        val mockWorkshops = List(5) {
            val offsetLat = Random.nextDouble(-0.01, 0.01)
            val offsetLng = Random.nextDouble(-0.01, 0.01)
            Workshop(
                id = it.toString(),
                name = "Mechanic ${'A' + it}",
                latitude = lat + offsetLat,
                longitude = lng + offsetLng,
                phone = "0300${Random.nextInt(1000000, 9999999)}",
                rating = Random.nextDouble(3.5, 5.0).toFloat(),
                distanceKm = String.format("%.2f", Random.nextDouble(0.5, 5.0)).toFloat()
            )
        }

        _nearbyWorkshops.value = mockWorkshops
    }

    fun setSelectedWorkshop(workshop: Workshop) {
        _selectedWorkshop.value = workshop
    }

    fun clearSelectedWorkshop() {
        _selectedWorkshop.value = null
    }
}
