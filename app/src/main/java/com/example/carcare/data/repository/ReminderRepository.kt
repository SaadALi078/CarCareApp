package com.example.carcare.data.repository

import com.example.carcare.data.Reminder
import com.example.carcare.data.ReminderStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor() {
    private val db = Firebase.firestore
    private val collection = db.collection("reminders")

    // ✅ Get all active reminders for a specific vehicle
    suspend fun getVehicleReminders(vehicleId: String): List<Reminder> {
        return try {
            collection
                .whereEqualTo("vehicleId", vehicleId)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(Reminder::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Get all reminders for a specific user
    suspend fun getUserReminders(userId: String): List<Reminder> {
        return try {
            collection
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(Reminder::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Add new reminder
    suspend fun addReminder(reminder: Reminder) {
        try {
            collection.add(reminder).await()
        } catch (e: Exception) {
            throw e
        }
    }

    // ✅ Update an existing reminder
    suspend fun updateReminder(reminder: Reminder) {
        try {
            collection.document(reminder.id).set(reminder).await()
        } catch (e: Exception) {
            throw e
        }
    }

    // ✅ Delete a reminder by ID
    suspend fun deleteReminder(reminderId: String) {
        try {
            collection.document(reminderId).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    // ✅ Mark a reminder as complete
    suspend fun markAsComplete(reminderId: String) {
        try {
            collection.document(reminderId)
                .update("status", ReminderStatus.COMPLETED)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}
