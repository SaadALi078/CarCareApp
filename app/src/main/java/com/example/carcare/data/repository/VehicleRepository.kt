package com.carcare.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VehicleRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun saveVehicle(
        make: String,
        model: String,
        year: String,
        plate: String,
        mileage: String
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

        val vehicleData = hashMapOf(
            "make" to make,
            "model" to model,
            "year" to year,
            "plate" to plate,
            "mileage" to mileage
        )

        return try {
            firestore.collection("users")
                .document(uid)
                .collection("vehicles")
                .add(vehicleData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
