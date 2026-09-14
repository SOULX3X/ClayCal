package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ClayPrimary,
    secondary = ClaySecondary,
    tertiary = ClayTertiary,
    background = Color(0xFF1E1A24),
    surface = Color(0xFF282332),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFECE7F2),
    onSurface = Color(0xFFECE7F2)
)

private val LightColorScheme = lightColorScheme(
    primary = ClayPrimary,
    secondary = ClaySecondary,
    tertiary = ClayTertiary,
    background = ClayBackground,
    surface = ClaySurface,
    onPrimary = ClayOnPrimary,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = ClayOnBackground,
    onSurface = ClayOnSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep tactile clay colors consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
