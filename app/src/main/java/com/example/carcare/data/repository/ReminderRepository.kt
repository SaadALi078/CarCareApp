// data/ReminderRepository.kt
package com.example.carcare.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor() {
    private val db = Firebase.firestore
    private val collection = db.collection("reminders")

    suspend fun getUserReminders(userId: String): List<Reminder> {
        return try {
            collection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.toObject(Reminder::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addReminder(reminder: Reminder): String {
        return try {
            val docRef = collection.add(reminder.toMap()).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add reminder: ${e.message}")
        }
    }

    suspend fun updateReminder(reminder: Reminder) {
        try {
            require(reminder.id.isNotEmpty()) { "Reminder ID must not be empty for update" }
            collection.document(reminder.id).set(reminder.toMap()).await()
        } catch (e: Exception) {
            throw Exception("Failed to update reminder: ${e.message}")
        }
    }

    suspend fun deleteReminder(reminderId: String) {
        try {
            collection.document(reminderId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete reminder: ${e.message}")
        }
    }

    suspend fun markAsComplete(reminderId: String) {
        try {
            collection.document(reminderId).update("isActive", false).await()
        } catch (e: Exception) {
            throw Exception("Failed to mark reminder as complete: ${e.message}")
        }
    }
}