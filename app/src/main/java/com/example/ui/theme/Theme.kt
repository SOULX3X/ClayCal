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
        tertiary = ClayColors.ClayMint,
        background = Color(0xFF16131C),
        surface = Color(0xFF25202E),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFFF3EDF8),
        onSurface = Color(0xFFF3EDF8)
    )

    val lightColorScheme = lightColorScheme(
        primary = primaryAccent,
        secondary = ClayColors.ClayLavender,
        tertiary = ClayColors.ClayMint,
        background = Color(0xFFF7F4EF),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFF2D2522),
        onSurface = Color(0xFF2D2522)
    )

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
