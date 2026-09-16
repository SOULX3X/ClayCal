package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppThemeMode
import com.example.data.CalendarDataTransfer
import com.example.data.ThemePreferences
import com.example.model.CalendarEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CalendarSettingsTest {

    @Test
    fun `theme preferences stores and retrieves theme mode and accent`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = ThemePreferences(context)

        prefs.themeMode = AppThemeMode.DARK
        assertEquals(AppThemeMode.DARK, prefs.themeMode)

        prefs.themeMode = AppThemeMode.LIGHT
        assertEquals(AppThemeMode.LIGHT, prefs.themeMode)

        prefs.accentPalette = "Lavender"
        assertEquals("Lavender", prefs.accentPalette)

        // New install default for tour should be false
        assertEquals(false, prefs.hasCompletedTour)
        prefs.hasCompletedTour = true
        assertEquals(true, prefs.hasCompletedTour)

        // Notification preferences
        prefs.notificationsEnabled = true
        assertEquals(true, prefs.notificationsEnabled)
        prefs.defaultReminderMinutes = 30
        assertEquals(30, prefs.defaultReminderMinutes)
    }

    @Test
    fun `event notification scheduler calculates correct trigger millis`() {
        val event = CalendarEvent(
            id = 1,
            title = "Team Standup",
            description = "Daily sync",
            category = "Work",
            year = 2026,
            month = 9,
            day = 16,
            startHour = 10,
            startMinute = 0,
            endHour = 10,
            endMinute = 30,
            colorHex = "#6B9B7A",
            location = "Room 3",
            reminderMinutesBefore = 15
        )

        val triggerMillis = com.example.notification.EventNotificationScheduler.calculateTriggerMillis(event)

        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, 2026)
            set(java.util.Calendar.MONTH, 8) // 0-based month for September
            set(java.util.Calendar.DAY_OF_MONTH, 16)
            set(java.util.Calendar.HOUR_OF_DAY, 10)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val expectedStart = calendar.timeInMillis
        val expectedTrigger = expectedStart - (15 * 60 * 1000L)

        assertEquals(expectedTrigger, triggerMillis)
    }

    @Test
    fun `export and import calendar data preserves event details`() {
        val originalEvents = listOf(
            CalendarEvent(
                id = 101,
                title = "Design Claymorphism Meeting",
                description = "Discuss tactile depth and highlights",
                category = "Work",
                year = 2026,
                month = 9,
                day = 20,
                startHour = 14,
                startMinute = 30,
                endHour = 15,
                endMinute = 30,
                colorHex = "#845EC2",
                location = "Clay Studio",
                isCompleted = false,
                priority = "High"
            ),
            CalendarEvent(
                id = 102,
                title = "Pottery Workshop",
                description = "Handcrafted clay cups",
                category = "Creative",
                year = 2026,
                month = 9,
                day = 21,
                startHour = 10,
                startMinute = 0,
                endHour = 12,
                endMinute = 0,
                colorHex = "#2EC4B6",
                location = "Art Center",
                isCompleted = true,
                priority = "Medium"
            )
        )

        val jsonString = CalendarDataTransfer.exportToJson(originalEvents)
        assertNotNull(jsonString)
        assertTrue(jsonString.contains("Design Claymorphism Meeting"))
        assertTrue(jsonString.contains("Pottery Workshop"))

        val importResult = CalendarDataTransfer.importFromJson(jsonString)
        assertTrue(importResult.isSuccess)

        val importedEvents = importResult.getOrThrow()
        assertEquals(2, importedEvents.size)

        val event1 = importedEvents.first { it.title == "Design Claymorphism Meeting" }
        assertEquals("Work", event1.category)
        assertEquals(2026, event1.year)
        assertEquals(9, event1.month)
        assertEquals(20, event1.day)
        assertEquals(14, event1.startHour)
        assertEquals(30, event1.startMinute)
        assertEquals("#845EC2", event1.colorHex)
        assertEquals("High", event1.priority)

        val event2 = importedEvents.first { it.title == "Pottery Workshop" }
        assertEquals(true, event2.isCompleted)
        assertEquals("Art Center", event2.location)
    }
}
