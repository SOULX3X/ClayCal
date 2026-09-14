package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.example.ui.clay.ClayColors

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Keep ClayColors.isDark in sync with Compose theme state
    ClayColors.isDark = darkTheme

    val primaryAccent = ClayColors.PrimaryAccent

    val darkColorScheme = darkColorScheme(
        primary = primaryAccent,
        secondary = ClayColors.ClayLavender,
        tertiary = ClayColors.ClaySage,
        background = Color(0xFF1C1A18),
        surface = Color(0xFF282522),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFFF6F3EE),
        onSurface = Color(0xFFF6F3EE)
    )

    val lightColorScheme = lightColorScheme(
        primary = primaryAccent,
        secondary = ClayColors.ClayLavender,
        tertiary = ClayColors.ClaySage,
        background = Color(0xFFEFEAE2),
        surface = Color(0xFFFAF7F2),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFF2E2724),
        onSurface = Color(0xFF2E2724)
    )

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
