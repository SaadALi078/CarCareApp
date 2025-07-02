package com.example.carcare.data.repository

import android.util.Log
import com.example.carcare.data.Vehicle
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VehicleRepository @Inject constructor() {
    private val firestore = Firebase.firestore
    private val collection = firestore.collection("vehicles")

    suspend fun getVehicles(): List<Vehicle> {
        return try {
            collection.get().await().documents.mapNotNull { doc ->
                doc.toObject(Vehicle::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addVehicle(vehicle: Vehicle) {
        try {
            val id = vehicle.id.ifBlank { System.currentTimeMillis().toString() }
            val vehicleWithId = vehicle.copy(id = id)
            collection.document(id).set(vehicleWithId).await()
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Failed to add vehicle", e)
        }
    }



    suspend fun updateVehicle(vehicle: Vehicle) {
        try {
            require(vehicle.id.isNotEmpty()) { "Vehicle ID must not be empty" }
            collection.document(vehicle.id).set(vehicle).await()
        } catch (e: Exception) {
            throw Exception("Failed to update vehicle: ${e.message}")
        }
    }

    suspend fun deleteVehicle(vehicleId: String) {
        try {
            collection.document(vehicleId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete vehicle: ${e.message}")
        }
    }
}