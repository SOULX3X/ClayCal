package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.clay.ClayColors

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Always light mode: clay reads as wet plastic on dark backgrounds
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Clay design rules mandate light backgrounds, never dark mode
    ClayColors.isDark = false

    val primaryAccent = ClayColors.PrimaryAccent

    val lightColorScheme = lightColorScheme(
        primary = primaryAccent,
        secondary = ClayColors.ClayLavender,
        tertiary = ClayColors.ClaySage,
        background = ClayColors.Background,
        surface = ClayColors.SurfaceMarshmallow,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = ClayColors.TextPrimary,
        onSurface = ClayColors.TextPrimary
    )

    MaterialTheme(
        colorScheme = lightColorScheme,
        typography = Typography,
        content = content
    )
}
