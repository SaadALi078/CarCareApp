package com.example.carcare.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.carcare.data.Reminder
import com.example.carcare.workers.ReminderNotificationWorker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor() {
    private val db = Firebase.firestore
    private val collection = db.collection("reminders")

    suspend fun getVehicleReminders(vehicleId: String): List<Reminder> {
        return try {
            val snapshot = collection
                .whereEqualTo("vehicleId", vehicleId)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Reminder::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserReminders(userId: String): List<Reminder> {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Reminder::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addReminder(reminder: Reminder) {
        collection.add(reminder).await()
    }

    suspend fun updateReminder(reminder: Reminder) {
        collection.document(reminder.id).set(reminder).await()
    }

    suspend fun deleteReminder(id: String) {
        collection.document(id).delete().await()
    }

    suspend fun markAsComplete(id: String) {
        collection.document(id).update("manualStatus", "COMPLETED").await()
    }

    fun scheduleReminderNotification(context: Context, reminder: Reminder) {
        val dueDate = reminder.dueDate.toDate().time
        val now = System.currentTimeMillis()

        // Only schedule if due date is in future
        if (dueDate > now) {
            val delay = dueDate - now

            val data = Data.Builder()
                .putString("title", "Maintenance Due: ${reminder.type}")
                .putString("message", "${reminder.vehicleName} - Due on ${reminder.dueDate.toDate()}")
                .build()

            val request = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
