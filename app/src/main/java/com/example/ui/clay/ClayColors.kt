package com.example.ui.clay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

data class ThemeColorItem(
    val name: String,
    val color: Color,
    val category: String, // "Bright & Vivid" or "Soft Pastel"
    val description: String = ""
)

object ClayColors {
    var isDark by mutableStateOf(false)

    // Current accent theme (default to Sage Green from design)
    var activeAccentName by mutableStateOf("Sage")

    // Clay background tones matching pastel claymorphism:
    // Light mode: warm soft pastel cream & porcelain marshmallow
    // Dark mode: velvety warm pastel slate plum
    val Background: Color
        get() = if (isDark) Color(0xFF1E1B22) else Color(0xFFF7F3EE)

    val SurfaceMarshmallow: Color
        get() = if (isDark) Color(0xFF292530) else Color(0xFFFCFAF7)

    val SurfaceSoftClay: Color
        get() = if (isDark) Color(0xFF231F2A) else Color(0xFFF2ECE5)

    val SurfaceDimmed: Color
        get() = if (isDark) Color(0xFF18151D) else Color(0xFFE8E1D8)

    // Clay borders & highlights (inner bevel glaze)
    val HighlightRim: Color
        get() = if (isDark) Color(0x38FFFFFF) else Color(0xD0FFFFFF)

    val ShadowBevel: Color
        get() = if (isDark) Color(0xFF131018) else Color(0xFFD6CFC4)

    val ShadowAmbient: Color
        get() = if (isDark) Color(0x75000000) else Color(0x223C3228)

    val ShadowSpot: Color
        get() = if (isDark) Color(0x95000000) else Color(0x1B261E17)

    // Bright Themes
    val BrightSunshine = Color(0xFFFFB300)   // Radiant Golden Sunshine
    val BrightCoral = Color(0xFFFF4D6D)      // Neon punchy Coral Pink
    val BrightRose = Color(0xFFF43F5E)       // Bubblegum Candy Rose
    val BrightViolet = Color(0xFF8B5CF6)     // Electric Lavender/Violet
    val BrightAqua = Color(0xFF06B6D4)       // Vibrant Electric Sky Aqua
    val BrightEmerald = Color(0xFF10B981)    // Vivid Spring Emerald
    val BrightTangerine = Color(0xFFFF7A00)  // Juicy Sunny Tangerine
    val BrightBerry = Color(0xFFD946EF)      // Fuchsia Berry Clay

    // Soft Pastel Themes
    val PastelSage = Color(0xFF5B8E6D)       // Fresh Pistachio / Sage Clay
    val PastelTerracotta = Color(0xFFE56B55) // Baked Terracotta
    val PastelPeach = Color(0xFFFFA67E)      // Warm Apricot / Peach
    val PastelLavender = Color(0xFF9E86E8)   // Soft Lilac / Lavender
    val PastelSky = Color(0xFF5BB2F0)        // Soft Baby Sky Blue
    val PastelMint = Color(0xFF48C794)       // Refreshing Mint Clay

    // Aliases for compatibility
    var ClaySage = PastelSage
    var ClayTerracotta = PastelTerracotta
    var ClayCoral = BrightCoral
    var ClayPeach = PastelPeach
    var ClayLavender = PastelLavender
    var ClaySoftBlue = PastelSky
    var ClayIndigo = BrightViolet
    var ClayMint = PastelMint
    var ClayAmber = BrightSunshine
    var ClayRose = BrightRose
    var ClaySky = PastelSky

    val PALETTES: List<ThemeColorItem> = listOf(
        // Bright & Vivid Themes
        ThemeColorItem("Sunshine", BrightSunshine, "Bright & Vivid", "Golden warmth"),
        ThemeColorItem("Coral", BrightCoral, "Bright & Vivid", "Punchy neon coral"),
        ThemeColorItem("Rose", BrightRose, "Bright & Vivid", "Vivid bubblegum"),
        ThemeColorItem("Violet", BrightViolet, "Bright & Vivid", "Electric purple"),
        ThemeColorItem("Aqua", BrightAqua, "Bright & Vivid", "Electric sky cyan"),
        ThemeColorItem("Emerald", BrightEmerald, "Bright & Vivid", "Vibrant spring green"),
        ThemeColorItem("Tangerine", BrightTangerine, "Bright & Vivid", "Juicy citrus"),
        ThemeColorItem("Berry", BrightBerry, "Bright & Vivid", "Fuchsia magenta"),
        // Soft Pastel Themes
        ThemeColorItem("Sage", PastelSage, "Soft Pastel", "Calming pistachio"),
        ThemeColorItem("Terracotta", PastelTerracotta, "Soft Pastel", "Warm baked clay"),
        ThemeColorItem("Peach", PastelPeach, "Soft Pastel", "Soft warm apricot"),
        ThemeColorItem("Lavender", PastelLavender, "Soft Pastel", "Dreamy lilac"),
        ThemeColorItem("Blue", PastelSky, "Soft Pastel", "Clear pastel sky"),
        ThemeColorItem("Mint", PastelMint, "Soft Pastel", "Cool herbal mint")
    )

    // Primary Brand Accent based on active theme
    val PrimaryAccent: Color
        get() = when (activeAccentName.trim().lowercase()) {
            "sunshine", "yellow" -> BrightSunshine
            "coral" -> BrightCoral
            "rose", "pink" -> BrightRose
            "violet", "purple", "indigo" -> BrightViolet
            "aqua", "cyan" -> BrightAqua
            "emerald", "green" -> BrightEmerald
            "tangerine", "orange" -> BrightTangerine
            "berry", "magenta" -> BrightBerry
            "terracotta" -> PastelTerracotta
            "lavender" -> PastelLavender
            "peach" -> PastelPeach
            "blue", "sky" -> PastelSky
            "mint" -> PastelMint
            else -> PastelSage
        }

    // Text colors
    val TextPrimary: Color
        get() = if (isDark) Color(0xFFF7F5F0) else Color(0xFF2C2522)

    val TextSecondary: Color
        get() = if (isDark) Color(0xFFB8B0A8) else Color(0xFF736962)

    val TextTertiary: Color
        get() = if (isDark) Color(0xFF8C837B) else Color(0xFFA59D95)

    val TextOnAccent = Color(0xFFFFFFFF)

    // Calendar Specific
    val WeekendText: Color
        get() = if (isDark) Color(0xFFFFA67E) else Color(0xFFE56B55)

    val TodayGlow: Color
        get() = if (isDark) PrimaryAccent.copy(alpha = 0.85f) else PrimaryAccent

    val GridLine: Color
        get() = if (isDark) Color(0x22FFFFFF) else Color(0x1242382E)

    // Danger Zone
    val DangerRed = Color(0xFFE56B55)
    val DangerBg: Color
        get() = if (isDark) Color(0x33E56B55) else Color(0x1AE56B55)
    val DangerBorder: Color
        get() = if (isDark) Color(0x66E56B55) else Color(0x40E56B55)
}
