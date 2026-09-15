package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class AppThemeMode(val key: String, val title: String) {
    SYSTEM("system", "System Default"),
    LIGHT("light", "Light Mode"),
    DARK("dark", "Dark Mode");

    companion object {
        fun fromKey(key: String): AppThemeMode =
            values().firstOrNull { it.key == key } ?: SYSTEM
    }
}

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("claycal_settings", Context.MODE_PRIVATE)

    var themeMode: AppThemeMode
        get() = AppThemeMode.fromKey(prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.key) ?: AppThemeMode.SYSTEM.key)
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value.key).apply()

    var accentPalette: String
        get() = prefs.getString(KEY_ACCENT, "Terracotta") ?: "Terracotta"
        set(value) = prefs.edit().putString(KEY_ACCENT, value).apply()

    var defaultEventsCleaned: Boolean
        get() = prefs.getBoolean(KEY_DEFAULT_EVENTS_CLEANED, false)
        set(value) = prefs.edit().putBoolean(KEY_DEFAULT_EVENTS_CLEANED, value).apply()

    var hasCompletedTour: Boolean
        get() = prefs.getBoolean(KEY_HAS_COMPLETED_TOUR, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_COMPLETED_TOUR, value).apply()

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ACCENT = "accent_palette"
        private const val KEY_DEFAULT_EVENTS_CLEANED = "default_events_cleaned"
        private const val KEY_HAS_COMPLETED_TOUR = "has_completed_tour"
    }
}
