package com.example.ui.clay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ClayColors {
    var isDark by mutableStateOf(false)

    // Current accent theme
    var activeAccentName by mutableStateOf("Terracotta")

    // Clay background tones
    val Background: Color
        get() = if (isDark) Color(0xFF16131C) else Color(0xFFF7F4EF)

    val SurfaceMarshmallow: Color
        get() = if (isDark) Color(0xFF25202E) else Color(0xFFFFFFFF)

    val SurfaceSoftClay: Color
        get() = if (isDark) Color(0xFF1E1926) else Color(0xFFFDFBF8)

    val SurfaceDimmed: Color
        get() = if (isDark) Color(0xFF2E273A) else Color(0xFFEFECE5)

    // Clay borders & highlights
    val HighlightRim: Color
        get() = if (isDark) Color(0x35FFFFFF) else Color(0xFFFFFFFF)

    val ShadowBevel: Color
        get() = if (isDark) Color(0xFF100D15) else Color(0xFFD8D0C5)

    val ShadowAmbient: Color
        get() = if (isDark) Color(0x66000000) else Color(0x334A3E3D)

    val ShadowSpot: Color
        get() = if (isDark) Color(0x88000000) else Color(0x282B2118)

    // Clay Accents
    var ClayTerracotta = Color(0xFFFF6B6B)
    var ClayPeach = Color(0xFFFF8E72)
    var ClayCoral = Color(0xFFFF7A59)
    var ClayLavender = Color(0xFF845EC2)
    var ClayIndigo = Color(0xFF4361EE)
    var ClayMint = Color(0xFF2EC4B6)
    var ClaySage = Color(0xFF54D396)
    var ClayAmber = Color(0xFFFF9F1C)
    var ClayRose = Color(0xFFE84393)
    var ClaySky = Color(0xFF4D80E6)

    // Primary Brand Accent based on active theme
    val PrimaryAccent: Color
        get() = when (activeAccentName) {
            "Lavender" -> ClayLavender
            "Mint" -> ClayMint
            "Peach" -> ClayPeach
            "Indigo" -> ClayIndigo
            else -> ClayTerracotta
        }

    // Text colors
    val TextPrimary: Color
        get() = if (isDark) Color(0xFFF3EDF8) else Color(0xFF2D2522)

    val TextSecondary: Color
        get() = if (isDark) Color(0xFFB4A8C2) else Color(0xFF7A6F68)

    val TextTertiary: Color
        get() = if (isDark) Color(0xFF827690) else Color(0xFFA89F98)

    val TextOnAccent = Color(0xFFFFFFFF)

    // Calendar Specific
    val WeekendText: Color
        get() = if (isDark) Color(0xFFFF8E72) else Color(0xFFE65100)

    val TodayGlow: Color
        get() = if (isDark) Color(0xFFFF7B7B) else Color(0xFFFF6B6B)

    val GridLine: Color
        get() = if (isDark) Color(0x2BFFFFFF) else Color(0x1A4A3E3D)

    // Danger Zone
    val DangerRed = Color(0xFFE63946)
    val DangerBg: Color
        get() = if (isDark) Color(0x33E63946) else Color(0x1AE63946)
    val DangerBorder: Color
        get() = if (isDark) Color(0x66E63946) else Color(0x40E63946)
}
