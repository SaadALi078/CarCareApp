package com.example.carcare.data

import retrofit2.http.GET
import retrofit2.http.Query

// In a new file, e.g., data/network/OverpassApiService.kt

// Data classes to hold the JSON response from the Overpass API
data class OverpassResponse(val elements: List<OverpassElement>)
data class OverpassElement(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, String>
)

// The Retrofit service interface
interface OverpassApiService {
    @GET("api/interpreter")
    suspend fun searchNearby(
        @Query("data") query: String
    ): OverpassResponse
}