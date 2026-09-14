package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {

    @Query("SELECT * FROM events ORDER BY year ASC, month ASC, day ASC, startHour ASC, startMinute ASC")
    fun getAllEvents(): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM events WHERE year = :year AND month = :month AND day = :day ORDER BY startHour ASC, startMinute ASC")
    fun getEventsForDate(year: Int, month: Int, day: Int): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM events WHERE year = :year AND month = :month ORDER BY day ASC, startHour ASC, startMinute ASC")
    fun getEventsForMonth(year: Int, month: Int): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Long): CalendarEvent?

    @Query("SELECT * FROM events ORDER BY year ASC, month ASC, day ASC, startHour ASC, startMinute ASC")
    suspend fun getAllEventsList(): List<CalendarEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<CalendarEvent>)

    @Update
    suspend fun updateEvent(event: CalendarEvent)

    @Delete
    suspend fun deleteEvent(event: CalendarEvent)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()

    @Query("UPDATE events SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletedStatus(id: Long, isCompleted: Boolean)
}
