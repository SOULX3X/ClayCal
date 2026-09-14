package com.example.data

import com.example.model.CalendarEvent
import com.example.model.SimpleDate
import org.json.JSONArray
import org.json.JSONObject

object CalendarDataTransfer {

    fun exportToJson(events: List<CalendarEvent>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "Claycal")
        root.put("exportedAt", System.currentTimeMillis())
        root.put("count", events.size)

        val eventsArray = JSONArray()
        for (event in events) {
            val obj = JSONObject()
            obj.put("title", event.title)
            obj.put("description", event.description)
            obj.put("category", event.category)
            obj.put("year", event.year)
            obj.put("month", event.month)
            obj.put("day", event.day)
            obj.put("startHour", event.startHour)
            obj.put("startMinute", event.startMinute)
            obj.put("endHour", event.endHour)
            obj.put("endMinute", event.endMinute)
            obj.put("colorHex", event.colorHex)
            obj.put("location", event.location)
            obj.put("isCompleted", event.isCompleted)
            obj.put("priority", event.priority)
            obj.put("reminderMinutesBefore", event.reminderMinutesBefore)
            obj.put("createdAt", event.createdAt)
            eventsArray.put(obj)
        }
        root.put("events", eventsArray)
        return root.toString(2)
    }

    fun importFromJson(jsonString: String): Result<List<CalendarEvent>> {
        return runCatching {
            val json = jsonString.trim()
            val eventsList = mutableListOf<CalendarEvent>()

            if (json.startsWith("[")) {
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    eventsList.add(parseEvent(obj))
                }
            } else {
                val root = JSONObject(json)
                val array = root.optJSONArray("events") ?: JSONArray()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    eventsList.add(parseEvent(obj))
                }
            }

            if (eventsList.isEmpty()) {
                throw IllegalArgumentException("No calendar events could be parsed from the provided JSON.")
            }
            eventsList
        }
    }

    private fun parseEvent(obj: JSONObject): CalendarEvent {
        val today = SimpleDate.today()
        return CalendarEvent(
            id = 0L,
            title = obj.optString("title", "Untitled Event").ifBlank { "Untitled Event" },
            description = obj.optString("description", ""),
            category = obj.optString("category", "Personal"),
            year = obj.optInt("year", today.year),
            month = obj.optInt("month", today.month).coerceIn(1, 12),
            day = obj.optInt("day", today.day).coerceIn(1, 31),
            startHour = obj.optInt("startHour", 9).coerceIn(0, 23),
            startMinute = obj.optInt("startMinute", 0).coerceIn(0, 59),
            endHour = obj.optInt("endHour", 10).coerceIn(0, 23),
            endMinute = obj.optInt("endMinute", 0).coerceIn(0, 59),
            colorHex = obj.optString("colorHex", "#FF6B6B"),
            location = obj.optString("location", ""),
            isCompleted = obj.optBoolean("isCompleted", false),
            priority = obj.optString("priority", "Medium"),
            reminderMinutesBefore = obj.optInt("reminderMinutesBefore", 15),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
        )
    }
}
