package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Personal",
    val year: Int,
    val month: Int,
    val day: Int,
    val startHour: Int = 9,
    val startMinute: Int = 0,
    val endHour: Int = 10,
    val endMinute: Int = 0,
    val colorHex: String = "#FF6B6B",
    val location: String = "",
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // "High", "Medium", "Low"
    val reminderMinutesBefore: Int = 15,
    val createdAt: Long = System.currentTimeMillis()
) {
    val date: SimpleDate
        get() = SimpleDate(year, month, day)

    val startTime: SimpleTime
        get() = SimpleTime(startHour, startMinute)

    val endTime: SimpleTime
        get() = SimpleTime(endHour, endMinute)

    val timeRangeFormatted: String
        get() = "${startTime.formatted()} - ${endTime.formatted()}"

    val durationFormatted: String
        get() {
            val startTotal = startHour * 60 + startMinute
            val endTotal = endHour * 60 + endMinute
            val diff = (endTotal - startTotal).let { if (it <= 0) it + 24 * 60 else it }
            val hours = diff / 60
            val minutes = diff % 60
            return when {
                hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
                hours > 0 -> "${hours}h"
                else -> "${minutes}m"
            }
        }
}
