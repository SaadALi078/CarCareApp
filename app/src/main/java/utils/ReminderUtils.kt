package com.example.carcare.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object ReminderUtils {

    fun calculateDelayFromDateTime(dateString: String, timeString: String): Long {
        // Combine date and time strings
        val fullDateTime = "$dateString $timeString"
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        return try {
            val targetDate = formatter.parse(fullDateTime)
            val now = Date()
            val delayMillis = targetDate?.time?.minus(now.time) ?: 0L

            if (delayMillis > 0) delayMillis else 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun formatTime(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    fun formatDate(day: Int, month: Int, year: Int): String {
        return String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year)
    }
}
