package com.example.data

import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.model.SimpleDate
import kotlinx.coroutines.flow.Flow

class CalendarRepository(private val dao: CalendarDao) {

    val allEvents: Flow<List<CalendarEvent>> = dao.getAllEvents()

    fun getEventsForDate(date: SimpleDate): Flow<List<CalendarEvent>> =
        dao.getEventsForDate(date.year, date.month, date.day)

    suspend fun insert(event: CalendarEvent): Long =
        dao.insertEvent(event)

    suspend fun update(event: CalendarEvent) =
        dao.updateEvent(event)

    suspend fun delete(event: CalendarEvent) =
        dao.deleteEvent(event)

    suspend fun deleteById(id: Long) =
        dao.deleteEventById(id)

    suspend fun deleteAll() =
        dao.deleteAllEvents()

    suspend fun getAllList(): List<CalendarEvent> =
        dao.getAllEventsList()

    suspend fun insertAll(events: List<CalendarEvent>) =
        dao.insertAll(events)

    suspend fun toggleCompleted(id: Long, isCompleted: Boolean) =
        dao.updateCompletedStatus(id, isCompleted)

    suspend fun ensureSampleData() {
        val today = SimpleDate.today()
        val tomorrow = today.addDays(1)
        val dayAfter = today.addDays(2)
        val yesterday = today.addDays(-1)
        val nextWeek = today.addDays(4)

        // Populate sample data if needed
        val existing = dao.getEventById(1)
        if (existing == null) {
            val sampleEvents = listOf(
                CalendarEvent(
                    id = 1,
                    title = "Clay Pottery & Sculpting",
                    description = "Hands-on tactile workshop exploring ceramic textures and clay sculpting.",
                    category = "Personal",
                    year = today.year,
                    month = today.month,
                    day = today.day,
                    startHour = 9,
                    startMinute = 30,
                    endHour = 11,
                    endMinute = 0,
                    colorHex = Category.Personal.hexColor,
                    location = "Artisan Studio B",
                    isCompleted = false,
                    priority = "Medium"
                ),
                CalendarEvent(
                    id = 2,
                    title = "Product Design Sprint",
                    description = "Review claymorphic design tokens, soft shadow elevations, and component library.",
                    category = "Work",
                    year = today.year,
                    month = today.month,
                    day = today.day,
                    startHour = 11,
                    startMinute = 30,
                    endHour = 12,
                    endMinute = 30,
                    colorHex = Category.Work.hexColor,
                    location = "Meeting Room Opal",
                    isCompleted = true,
                    priority = "High"
                ),
                CalendarEvent(
                    id = 3,
                    title = "Matcha Latte & Catch-up",
                    description = "Coffee break with Sarah to brainstorm new quarterly themes.",
                    category = "Social",
                    year = today.year,
                    month = today.month,
                    day = today.day,
                    startHour = 14,
                    startMinute = 0,
                    endHour = 15,
                    endMinute = 0,
                    colorHex = Category.Social.hexColor,
                    location = "Boulangerie Bakery",
                    isCompleted = false,
                    priority = "Low"
                ),
                CalendarEvent(
                    id = 4,
                    title = "Sunset Pilates Routine",
                    description = "Stretch and core balance mindfulness routine.",
                    category = "Health",
                    year = today.year,
                    month = today.month,
                    day = today.day,
                    startHour = 17,
                    startMinute = 30,
                    endHour = 18,
                    endMinute = 30,
                    colorHex = Category.Health.hexColor,
                    location = "Wellness Garden",
                    isCompleted = false,
                    priority = "Medium"
                ),
                CalendarEvent(
                    id = 5,
                    title = "Quarterly Strategy Review",
                    description = "Executive briefing on roadmap priorities and feature delivery timelines.",
                    category = "Work",
                    year = tomorrow.year,
                    month = tomorrow.month,
                    day = tomorrow.day,
                    startHour = 10,
                    startMinute = 0,
                    endHour = 11,
                    endMinute = 30,
                    colorHex = Category.Work.hexColor,
                    location = "Boardroom 3",
                    isCompleted = false,
                    priority = "High"
                ),
                CalendarEvent(
                    id = 6,
                    title = "Farmers Market Grocery Run",
                    description = "Pick up fresh heirloom vegetables, sourdough loaf, and local honey.",
                    category = "Task",
                    year = tomorrow.year,
                    month = tomorrow.month,
                    day = tomorrow.day,
                    startHour = 16,
                    startMinute = 0,
                    endHour = 17,
                    endMinute = 0,
                    colorHex = Category.Task.hexColor,
                    location = "Town Square Market",
                    isCompleted = false,
                    priority = "Medium"
                ),
                CalendarEvent(
                    id = 7,
                    title = "Architecture Deep Dive",
                    description = "Sync with mobile engineering leads on offline cache sync and database performance.",
                    category = "Study",
                    year = dayAfter.year,
                    month = dayAfter.month,
                    day = dayAfter.day,
                    startHour = 13,
                    startMinute = 0,
                    endHour = 14,
                    endMinute = 30,
                    colorHex = Category.Study.hexColor,
                    location = "Virtual Call",
                    isCompleted = false,
                    priority = "High"
                ),
                CalendarEvent(
                    id = 8,
                    title = "Family Brunch Sunday",
                    description = "Homemade waffles and garden picnic with the family.",
                    category = "Social",
                    year = nextWeek.year,
                    month = nextWeek.month,
                    day = nextWeek.day,
                    startHour = 11,
                    startMinute = 0,
                    endHour = 13,
                    endMinute = 0,
                    colorHex = Category.Social.hexColor,
                    location = "Sunny Patio",
                    isCompleted = false,
                    priority = "Medium"
                ),
                CalendarEvent(
                    id = 9,
                    title = "Evening Reading Hour",
                    description = "Chapter 4: Spatial UI and Tactile Digital Experiences.",
                    category = "Personal",
                    year = yesterday.year,
                    month = yesterday.month,
                    day = yesterday.day,
                    startHour = 20,
                    startMinute = 0,
                    endHour = 21,
                    endMinute = 0,
                    colorHex = Category.Personal.hexColor,
                    location = "Reading Nook",
                    isCompleted = true,
                    priority = "Low"
                )
            )
            dao.insertAll(sampleEvents)
        }
    }
}
