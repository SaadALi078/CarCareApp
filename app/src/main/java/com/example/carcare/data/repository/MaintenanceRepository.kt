package com.example.carcare.data.repository

import com.example.carcare.data.MaintenanceRecord
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaintenanceRepository @Inject constructor() {

    private val db = Firebase.firestore
    private val collection = db.collection("maintenance_records")

    // ✅ Get count of maintenance records for a vehicle
    suspend fun getMaintenanceCount(vehicleId: String): Int {
        return try {
            collection
                .whereEqualTo("vehicleId", vehicleId)
                .get()
                .await()
                .size()
        } catch (e: Exception) {
            0
        }
    }

    // ✅ Get recent N maintenance records for a vehicle
    suspend fun getRecentMaintenance(vehicleId: String, limit: Int): List<MaintenanceRecord> {
        return try {
            collection
                .whereEqualTo("vehicleId", vehicleId)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(MaintenanceRecord::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Get records by vehicleId and optional category (type)
    suspend fun getRecordsByVehicleAndCategory(
        vehicleId: String,
        category: String?
    ): List<MaintenanceRecord> {
        return try {
            var query = collection.whereEqualTo("vehicleId", vehicleId)
            category?.let {
                query = query.whereEqualTo("type", it)
            }
            query.get().await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(MaintenanceRecord::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Get a single record by ID
    suspend fun getRecord(recordId: String): MaintenanceRecord? {
        return try {
            val doc = collection.document(recordId).get().await()
            doc.toObject(MaintenanceRecord::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // ✅ Add a new maintenance record
    suspend fun addRecord(record: MaintenanceRecord): String {
        return try {
            val docRef = collection.add(record).await()
            docRef.id
        } catch (e: Exception) {
            throw e
        }
    }

    // ✅ Update an existing maintenance record
    suspend fun updateRecord(record: MaintenanceRecord) {
        if (record.id.isBlank()) return
        try {
            collection.document(record.id).set(record).await()
        } catch (e: Exception) {
            throw e
        }
    }
}
