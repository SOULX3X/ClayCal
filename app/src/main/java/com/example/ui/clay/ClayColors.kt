package com.example.ui.clay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ClayColors {
    var isDark by mutableStateOf(false)

    // Current accent theme (default to Sage Green from design)
    var activeAccentName by mutableStateOf("Sage")

    // Clay background tones matching reference design
    val Background: Color
        get() = if (isDark) Color(0xFF1C1A18) else Color(0xFFEFEAE2)

    val SurfaceMarshmallow: Color
        get() = if (isDark) Color(0xFF282522) else Color(0xFFFAF7F2)

    val SurfaceSoftClay: Color
        get() = if (isDark) Color(0xFF22201D) else Color(0xFFF5F1EA)

    val SurfaceDimmed: Color
        get() = if (isDark) Color(0xFF191816) else Color(0xFFE5DFD5)

    // Clay borders & highlights
    val HighlightRim: Color
        get() = if (isDark) Color(0x35FFFFFF) else Color(0xFFFFFFFF)

    val ShadowBevel: Color
        get() = if (isDark) Color(0xFF141311) else Color(0xFFD8D1C7)

    val ShadowAmbient: Color
        get() = if (isDark) Color(0x66000000) else Color(0x2442382E)

    val ShadowSpot: Color
        get() = if (isDark) Color(0x88000000) else Color(0x1F2B241E)

    // Clay Accents matching reference screens exactly
    var ClaySage = Color(0xFF4D6A56)       // Primary buttons, FAB, active tabs
    var ClayTerracotta = Color(0xFFDE7767) // Save event, Math revision, delete
    var ClayCoral = Color(0xFFDE7767)      // Alias for Terracotta/Coral
    var ClayPeach = Color(0xFFF3A882)      // Warm peach
    var ClayLavender = Color(0xFF8B80B6)   // Personal / Read
    var ClaySoftBlue = Color(0xFF7097B6)   // Work / Project
    var ClayIndigo = Color(0xFF5E72E4)
    var ClayMint = Color(0xFF698B71)       // Health / Gym
    var ClayAmber = Color(0xFFE5A869)      // Other / Social
    var ClayRose = Color(0xFFD9637E)
    var ClaySky = Color(0xFF7097B6)

    // Primary Brand Accent based on active theme
    val PrimaryAccent: Color
        get() = when (activeAccentName) {
            "Terracotta" -> ClayTerracotta
            "Lavender" -> ClayLavender
            "Peach" -> ClayPeach
            "Blue" -> ClaySoftBlue
            "Mint" -> ClayMint
            else -> ClaySage
        }

    // Text colors
    val TextPrimary: Color
        get() = if (isDark) Color(0xFFF6F3EE) else Color(0xFF2E2724)

    val TextSecondary: Color
        get() = if (isDark) Color(0xFFB5ACA3) else Color(0xFF756C65)

    val TextTertiary: Color
        get() = if (isDark) Color(0xFF887F77) else Color(0xFFA69E96)

    val TextOnAccent = Color(0xFFFFFFFF)

    // Calendar Specific
    val WeekendText: Color
        get() = if (isDark) Color(0xFFF3A882) else Color(0xFFD46A5B)

    val TodayGlow: Color
        get() = if (isDark) Color(0xFF6B8F77) else Color(0xFF4D6A56)

    val GridLine: Color
        get() = if (isDark) Color(0x22FFFFFF) else Color(0x1442382E)

    // Danger Zone
    val DangerRed = Color(0xFFDE7767)
    val DangerBg: Color
        get() = if (isDark) Color(0x33DE7767) else Color(0x1ADE7767)
    val DangerBorder: Color
        get() = if (isDark) Color(0x66DE7767) else Color(0x40DE7767)
}
