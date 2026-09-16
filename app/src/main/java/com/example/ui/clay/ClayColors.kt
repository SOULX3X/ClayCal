package com.example.ui.clay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

data class ThemeColorItem(
    val name: String,
    val color: Color,
    val category: String, // "Bright & Vivid" or "Soft Pastel"
    val description: String = ""
)

/**
 * Calculates a 20% darker shade of the exact same hue.
 * Used for the bottom-right inset shadow and the moulded outer drop shadow.
 */
fun Color.darker(factor: Float = 0.20f): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    // Reduce lightness by factor while preserving hue and saturation
    hsl[2] = (hsl[2] * (1f - factor)).coerceIn(0.05f, 0.95f)
    val darkArgb = ColorUtils.HSLToColor(hsl)
    return Color(darkArgb).copy(alpha = this.alpha)
}

/**
 * Calculates a soft lighter highlight of the same hue.
 * Used for top-left light inset highlight.
 */
fun Color.lighter(factor: Float = 0.25f): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] = (hsl[2] + (1f - hsl[2]) * factor).coerceIn(0.05f, 0.98f)
    val lightArgb = ColorUtils.HSLToColor(hsl)
    return Color(lightArgb).copy(alpha = this.alpha)
}

object ClayColors {
    // Light backgrounds only - clay reads as wet plastic on dark backgrounds
    var isDark by mutableStateOf(false)

    // Current accent theme
    var activeAccentName by mutableStateOf("Sage")

    // Warm, soft light porcelain & marshmallow canvas
    val Background: Color
        get() = Color(0xFFFBF8F4)

    val SurfaceMarshmallow: Color
        get() = Color(0xFFFFFFFF)

    val SurfaceSoftClay: Color
        get() = Color(0xFFF4ECE4)

    val SurfaceDimmed: Color
        get() = Color(0xFFEAE1D7)

    // Default neutral shadow tones (warm sandy clay, never cold grey)
    val DefaultShadowTone: Color
        get() = Color(0xFFD3C6B8)

    val ShadowBevel: Color
        get() = DefaultShadowTone

    val ShadowAmbient: Color
        get() = Color(0x2E8C7662)

    val ShadowSpot: Color
        get() = Color(0x3B6B5542)

    // Bright Candy Themes
    val BrightSunshine = Color(0xFFFFB703)   // Candy Banana Sunshine
    val BrightCoral = Color(0xFFFF5470)      // Candy Strawberry Coral
    val BrightRose = Color(0xFFFF5D8F)       // Bubblegum Candy Rose
    val BrightViolet = Color(0xFF8F65FF)     // Electric Candy Lavender
    val BrightAqua = Color(0xFF00C2CB)       // Candy Sky Cyan
    val BrightEmerald = Color(0xFF10B981)    // Vibrant Candy Mint
    val BrightTangerine = Color(0xFFFF7A00)  // Juicy Candy Tangerine
    val BrightBerry = Color(0xFFD946EF)      // Candy Berry Magenta

    // Soft Pastel Themes
    val PastelSage = Color(0xFF6B9B7A)       // Fresh Pistachio / Sage
    val PastelTerracotta = Color(0xFFE87A64) // Baked Warm Terracotta
    val PastelPeach = Color(0xFFFFAC87)      // Soft Apricot / Peach
    val PastelLavender = Color(0xFFA58FEF)   // Dreamy Soft Lilac
    val PastelSky = Color(0xFF60B8F5)        // Soft Baby Blue
    val PastelMint = Color(0xFF52D19D)       // Sweet Herbal Mint

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
        // Soft Pastel Themes
        ThemeColorItem("Sage", PastelSage, "Soft Pastel", "Calming pistachio"),
        ThemeColorItem("Peach", PastelPeach, "Soft Pastel", "Soft warm apricot"),
        ThemeColorItem("Lavender", PastelLavender, "Soft Pastel", "Dreamy lilac"),
        ThemeColorItem("Blue", PastelSky, "Soft Pastel", "Clear pastel sky"),
        ThemeColorItem("Mint", PastelMint, "Soft Pastel", "Cool herbal mint"),
        ThemeColorItem("Terracotta", PastelTerracotta, "Soft Pastel", "Warm baked clay"),
        // Bright & Vivid Themes
        ThemeColorItem("Coral", BrightCoral, "Bright & Vivid", "Punchy candy coral"),
        ThemeColorItem("Sunshine", BrightSunshine, "Bright & Vivid", "Golden sunshine"),
        ThemeColorItem("Rose", BrightRose, "Bright & Vivid", "Vivid bubblegum"),
        ThemeColorItem("Violet", BrightViolet, "Bright & Vivid", "Candy purple"),
        ThemeColorItem("Aqua", BrightAqua, "Bright & Vivid", "Electric cyan"),
        ThemeColorItem("Emerald", BrightEmerald, "Bright & Vivid", "Vivid spring green"),
        ThemeColorItem("Tangerine", BrightTangerine, "Bright & Vivid", "Juicy citrus"),
        ThemeColorItem("Berry", BrightBerry, "Bright & Vivid", "Fuchsia berry")
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

    // Text colors - warm dark chocolate and espresso clay
    val TextPrimary: Color
        get() = Color(0xFF2E241E)

    val TextSecondary: Color
        get() = Color(0xFF7A6B60)

    val TextTertiary: Color
        get() = Color(0xFFA6988D)

    val TextOnAccent = Color(0xFFFFFFFF)

    // Calendar Specific
    val WeekendText: Color
        get() = Color(0xFFE87A64)

    val TodayGlow: Color
        get() = PrimaryAccent

    val GridLine: Color
        get() = Color(0x144A3C30)

    // Danger Zone
    val DangerRed = Color(0xFFE5604B)
    val DangerBg: Color
        get() = Color(0x1AE5604B)
    val DangerBorder: Color
        get() = Color(0x33E5604B)
}
