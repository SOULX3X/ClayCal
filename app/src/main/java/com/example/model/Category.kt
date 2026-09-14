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
        val Study = Category(
            name = "Study",
            iconEmoji = "📚",
            clayColor = Color(0xFFDE7767),
            surfaceColor = Color(0xFFFBF0EE),
            hexColor = "#DE7767"
        )
        val Health = Category(
            name = "Health",
            iconEmoji = "🏃",
            clayColor = Color(0xFF698B71),
            surfaceColor = Color(0xFFF0F5F1),
            hexColor = "#698B71"
        )
        val Personal = Category(
            name = "Personal",
            iconEmoji = "🌿",
            clayColor = Color(0xFF8B80B6),
            surfaceColor = Color(0xFFF3F1F8),
            hexColor = "#8B80B6"
        )
        val Work = Category(
            name = "Work",
            iconEmoji = "💼",
            clayColor = Color(0xFF7097B6),
            surfaceColor = Color(0xFFEEF3F7),
            hexColor = "#7097B6"
        )
        val Social = Category(
            name = "Social",
            iconEmoji = "🎉",
            clayColor = Color(0xFFE5A869),
            surfaceColor = Color(0xFFFAF4ED),
            hexColor = "#E5A869"
        )
        val Other = Category(
            name = "Other",
            iconEmoji = "✨",
            clayColor = Color(0xFFF3A882),
            surfaceColor = Color(0xFFFAF2EE),
            hexColor = "#F3A882"
        )

        val ALL = listOf(Study, Health, Personal, Work, Social, Other)

        fun fromName(name: String): Category {
            return ALL.find { it.name.equals(name, ignoreCase = true) } ?: Personal
        }
    }
}
