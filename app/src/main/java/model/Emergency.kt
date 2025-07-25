// You ciman place this in a new file, e.g., model/Emergency.kt
package com.example.carcare.data.model
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

// A sealed class to define the specific categories you support
sealed class PoiCategory(
    val displayName: String,
    val icon: ImageVector,
    val osmQueryTag: Pair<String, String> // The tag used to query OpenStreetMap
) {
    object Workshop : PoiCategory("Workshop", Icons.Default.Build, "shop" to "car_repair")
    object AutoParts : PoiCategory("Auto Parts", Icons.Default.Settings, "shop" to "car_parts")
    object CarWash : PoiCategory("Car Wash", Icons.Default.LocalCarWash, "amenity" to "car_wash")
    object FuelStation : PoiCategory("Fuel Station", Icons.Default.LocalGasStation, "amenity" to "fuel")
}