package com.example.carcare.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MaintenanceRepository {
    private val db = Firebase.firestore
    private val collection = db.collection("maintenance_records")

    suspend fun getRecordsByCategory(category: String?): List<MaintenanceRecord> {
        return try {
            val query = category?.let {
                collection.whereEqualTo("category", it)
            } ?: collection

            query.get().await().documents.map { doc ->
                doc.toObject(MaintenanceRecord::class.java)?.copy(id = doc.id) ?: MaintenanceRecord()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecord(recordId: String): MaintenanceRecord {
        return try {
            collection.document(recordId).get().await()
                .toObject(MaintenanceRecord::class.java)?.copy(id = recordId)
                ?: MaintenanceRecord()
        } catch (e: Exception) {
            MaintenanceRecord()
        }
    }

    suspend fun addRecord(record: MaintenanceRecord) {
        collection.add(record).await()
    }

    suspend fun updateRecord(record: MaintenanceRecord) {
        require(record.id.isNotEmpty()) { "Record ID must not be empty for update" }
        collection.document(record.id).set(record).await()
    }
}
