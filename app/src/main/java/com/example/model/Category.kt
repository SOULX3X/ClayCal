package com.example.model

import androidx.compose.ui.graphics.Color

data class Category(
    val name: String,
    val iconEmoji: String,
    val clayColor: Color,
    val surfaceColor: Color,
    val hexColor: String
) {
    companion object {
        val Work = Category(
            name = "Work",
            iconEmoji = "💼",
            clayColor = Color(0xFF4361EE),
            surfaceColor = Color(0xFFEEF2FF),
            hexColor = "#4361EE"
        )
        val Personal = Category(
            name = "Personal",
            iconEmoji = "🌿",
            clayColor = Color(0xFFFF6B6B),
            surfaceColor = Color(0xFFFFF0F0),
            hexColor = "#FF6B6B"
        )
        val Health = Category(
            name = "Health",
            iconEmoji = "🏃",
            clayColor = Color(0xFF2EC4B6),
            surfaceColor = Color(0xFFE8FAF7),
            hexColor = "#2EC4B6"
        )
        val Social = Category(
            name = "Social",
            iconEmoji = "🎉",
            clayColor = Color(0xFFFF9F1C),
            surfaceColor = Color(0xFFFFF5E6),
            hexColor = "#FF9F1C"
        )
        val Study = Category(
            name = "Study",
            iconEmoji = "📚",
            clayColor = Color(0xFF845EC2),
            surfaceColor = Color(0xFFF3EDFB),
            hexColor = "#845EC2"
        )
        val Task = Category(
            name = "Task",
            iconEmoji = "⚡",
            clayColor = Color(0xFFE84393),
            surfaceColor = Color(0xFFFDE8F3),
            hexColor = "#E84393"
        )

        val ALL = listOf(Work, Personal, Health, Social, Study, Task)

        fun fromName(name: String): Category {
            return ALL.find { it.name.equals(name, ignoreCase = true) } ?: Personal
        }
    }
}
