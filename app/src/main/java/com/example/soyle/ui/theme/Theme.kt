package com.example.soyle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SoyleLightColors = lightColorScheme(
    primary = Primary,
    onPrimary = Surface,
    primaryContainer = Primary,
    onPrimaryContainer = Surface,
    secondary = Accent,
    onSecondary = Surface,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    outlineVariant = Border,
    error = Error,
    onError = Surface
)

@Composable
fun SoyleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SoyleLightColors,
        typography = SoyleTypography,
        content = content
    )
}
