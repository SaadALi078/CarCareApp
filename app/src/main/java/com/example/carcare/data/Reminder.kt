// data/Reminder.kt
package com.example.carcare.data

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.util.Date

data class Reminder(
    @get:Exclude var id: String = "",
    val userId: String = "",
    val vehicleId: String = "",
    val vehicleName: String = "",
    val type: String = "",
    val dueDate: Timestamp = Timestamp.now(),
    val odometerThreshold: Int = 0,
    val notes: String = "",
    val isActive: Boolean = true,
    val repeatInterval: Int? = null, // in months
    val notificationDaysBefore: Int = 0,
    val notificationKmBefore: Int = 0
)
{
    @get:Exclude
    val status: ReminderStatus
        get() {
            val now = Date()
            val dueDateObj = dueDate.toDate()
            val daysDiff = (dueDateObj.time - now.time) / (1000 * 60 * 60 * 24)

            return when {
                daysDiff < 0 -> ReminderStatus.OVERDUE
                daysDiff == 0L -> ReminderStatus.DUE_TODAY
                daysDiff <= 3 -> ReminderStatus.UPCOMING_SOON
                else -> ReminderStatus.UPCOMING
            }
        }

    @get:Exclude
    val statusColor: Color
        get() = when (status) {
            ReminderStatus.OVERDUE -> Color(0xFFF44336)
            ReminderStatus.DUE_TODAY -> Color(0xFFFFC107)
            ReminderStatus.UPCOMING_SOON -> Color(0xFFFF9800)
            ReminderStatus.UPCOMING -> Color(0xFF4CAF50)
        }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "vehicleId" to vehicleId,
            "vehicleName" to vehicleName,
            "type" to type,
            "dueDate" to dueDate,
            "odometerThreshold" to odometerThreshold,
            "notes" to notes,
            "isActive" to isActive,
            "repeatInterval" to (repeatInterval ?: ""),
            "notificationDaysBefore" to notificationDaysBefore,
            "notificationKmBefore" to notificationKmBefore
        )
    }
}

enum class ReminderStatus {
    OVERDUE, DUE_TODAY, UPCOMING_SOON, UPCOMING
}