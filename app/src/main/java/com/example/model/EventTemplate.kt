package com.example.model

data class EventTemplate(
    val title: String,
    val description: String,
    val category: String,
    val durationHours: Int,
    val durationMinutes: Int,
    val location: String,
    val priority: String
) {
    companion object {
        val PRESETS = listOf(
            EventTemplate(
                title = "Team Daily Standup",
                description = "Quick sync on blockers and sprint goals",
                category = "Work",
                durationHours = 0,
                durationMinutes = 30,
                location = "Google Meet",
                priority = "Medium"
            ),
            EventTemplate(
                title = "Cardio & Weight Training",
                description = "Leg day and 20 min interval running",
                category = "Health",
                durationHours = 1,
                durationMinutes = 0,
                location = "City Fitness Center",
                priority = "High"
            ),
            EventTemplate(
                title = "Deep Work & Focus",
                description = "No distraction coding and architecture planning",
                category = "Study",
                durationHours = 2,
                durationMinutes = 0,
                location = "Desk / Home Office",
                priority = "High"
            ),
            EventTemplate(
                title = "Coffee Catch-up",
                description = "Catching up on personal projects and life",
                category = "Social",
                durationHours = 1,
                durationMinutes = 0,
                location = "Artisan Bakery & Cafe",
                priority = "Low"
            ),
            EventTemplate(
                title = "Doctor / Health Checkup",
                description = "Annual routine medical checkup",
                category = "Health",
                durationHours = 1,
                durationMinutes = 15,
                location = "Community Clinic",
                priority = "High"
            ),
            EventTemplate(
                title = "Groceries & Weekly Prep",
                description = "Restock fresh vegetables and weekly ingredients",
                category = "Personal",
                durationHours = 1,
                durationMinutes = 0,
                location = "Organic Market",
                priority = "Normal"
            )
        )
    }
}
