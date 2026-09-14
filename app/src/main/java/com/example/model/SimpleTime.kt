package com.example.model

import java.util.Calendar
import java.util.Locale

data class SimpleTime(
    val hour: Int,   // 0..23
    val minute: Int  // 0..59
) : Comparable<SimpleTime> {

    companion object {
        fun now(): SimpleTime {
            val cal = Calendar.getInstance()
            return SimpleTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        }

        fun fromString(str: String): SimpleTime {
            val parts = str.split(":")
            if (parts.size == 2) {
                val h = parts[0].toIntOrNull() ?: 9
                val m = parts[1].toIntOrNull() ?: 0
                return SimpleTime(h.coerceIn(0, 23), m.coerceIn(0, 59))
            }
            return SimpleTime(9, 0)
        }
    }

    val isAm: Boolean get() = hour < 12

    val hour12: Int
        get() {
            val h = hour % 12
            return if (h == 0) 12 else h
        }

    fun formatted(): String {
        val amPm = if (isAm) "AM" else "PM"
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
    }

    fun formatted24(): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    fun addHours(hours: Int): SimpleTime {
        val totalMinutes = (hour * 60 + minute + hours * 60) % (24 * 60)
        val nonNegative = if (totalMinutes < 0) totalMinutes + 24 * 60 else totalMinutes
        return SimpleTime(nonNegative / 60, nonNegative % 60)
    }

    override fun compareTo(other: SimpleTime): Int {
        if (hour != other.hour) return hour.compareTo(other.hour)
        return minute.compareTo(other.minute)
    }
}
