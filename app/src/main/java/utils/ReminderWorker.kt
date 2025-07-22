package com.example.carcare.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.carcare.R
import com.example.carcare.utils.SettingsKeys
import com.example.carcare.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Random

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            // Retrieve input data
            val title = inputData.getString("title") ?: "Maintenance Reminder"
            val message = inputData.getString("message") ?: "Your vehicle needs attention"

            // Check notification preferences
            val showNotification = runBlocking {
                context.dataStore.data.first()[booleanPreferencesKey(SettingsKeys.MAINTENANCE_REMINDERS)] ?: true
            }

            if (!showNotification) {
                return Result.success()
            }

            // Show notification
            showNotification(title, message)
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "maintenance_reminders"
        val notificationId = Random().nextInt()

        // Create notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Maintenance Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for vehicle maintenance reminders"
            }
            manager.createNotificationChannel(channel)
        }

        // Check notification permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            manager.notify(notificationId, notification)
        }
    }
}