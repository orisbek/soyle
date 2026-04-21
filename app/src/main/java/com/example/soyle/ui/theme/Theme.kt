package com.example.soyle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SoyleDarkColorScheme = darkColorScheme(
    primary            = SoyleAccent,
    onPrimary          = Color.White,
    primaryContainer   = SoyleAccentSoft,
    secondary          = SoyleTextSecondary,
    onSecondary        = Color.White,
    background         = SoyleBg,
    onBackground       = SoyleTextPrimary,
    surface            = SoyleSurface,
    onSurface          = SoyleTextPrimary,
    surfaceVariant     = SoyleSurface2,
    onSurfaceVariant   = SoyleTextSecondary,
    outline            = SoyleBorder,
    outlineVariant     = SoyleBorderLight,
    error              = SoyleRed,
    onError            = Color.White
)

@Composable
fun SoyleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SoyleDarkColorScheme,
        typography  = SoyleTypography,
        content     = content
    )
}