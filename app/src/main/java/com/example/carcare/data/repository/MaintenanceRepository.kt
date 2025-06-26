package com.example.carcare.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaintenanceRepository @Inject constructor() {
    private val db = Firebase.firestore
    private val collection = db.collection("maintenance_records")

    suspend fun getAllRecords(): List<MaintenanceRecord> {
        return try {
            collection.get().await().documents.mapNotNull { doc ->
                doc.toObject(MaintenanceRecord::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecordsByCategory(category: String?): List<MaintenanceRecord> {
        return try {
            val query = category?.let {
                collection.whereEqualTo("type", it) // Changed from "category" to "type" to match your record structure
            } ?: collection

            query.get().await().documents.mapNotNull { doc ->
                doc.toObject(MaintenanceRecord::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecord(recordId: String): MaintenanceRecord? {
        return try {
            collection.document(recordId).get().await()
                .toObject(MaintenanceRecord::class.java)?.copy(id = recordId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addRecord(record: MaintenanceRecord): String {
        return try {
            val docRef = collection.add(record).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add record: ${e.message}")
        }
    }

    suspend fun updateRecord(record: MaintenanceRecord) {
        try {
            require(record.id.isNotEmpty()) { "Record ID must not be empty for update" }
            collection.document(record.id).set(record).await()
        } catch (e: Exception) {
            throw Exception("Failed to update record: ${e.message}")
        }
    }

    suspend fun deleteRecord(recordId: String) {
        try {
            collection.document(recordId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete record: ${e.message}")
        }
    }
}