package com.example.soyle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SoyleLightColors = lightColorScheme(
    primary            = DuoGreen,
    onPrimary          = DuoWhite,
    primaryContainer   = DuoGreenLight,
    onPrimaryContainer = DuoGreenDark,

    secondary          = DuoBlue,
    onSecondary        = DuoWhite,
    secondaryContainer = DuoBlueLight,
    onSecondaryContainer = DuoBlueDark,

    tertiary           = DuoYellow,
    onTertiary         = DuoTextPrimary,
    tertiaryContainer  = DuoYellowLight,

    background         = DuoBg,
    onBackground       = DuoTextPrimary,

    surface            = DuoCardBg,
    onSurface          = DuoTextPrimary,
    onSurfaceVariant   = DuoTextSecondary,
    outline            = DuoBorder,
    outlineVariant     = DuoBorder,

    error              = DuoRed,
    onError            = DuoWhite,
    errorContainer     = DuoRedLight,
)

@Composable
fun SoyleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SoyleLightColors,
        typography  = SoyleTypography,
        content     = content
    )
}