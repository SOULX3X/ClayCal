package com.example.data

import com.example.model.CalendarEvent
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

    suspend fun removeDefaultSampleEvents() {
        val sampleTitles = setOf(
            "Clay Pottery & Sculpting",
            "Product Design Sprint",
            "Morning Espresso & Planning",
            "Tactile Clay Sculpting Meetup",
            "Team Retro & Sprint Demo",
            "Sunset Yoga & Stretch",
            "Architecture Deep Dive",
            "Family Brunch Sunday",
            "Evening Reading Hour"
        )
        val all = dao.getAllEventsList()
        val defaultEvents = all.filter { it.title in sampleTitles || it.id in 1L..9L }
        defaultEvents.forEach {
            dao.deleteEvent(it)
        }
    }
}
