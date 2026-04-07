package com.example.soyle.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KidsColorScheme = lightColorScheme(
    primary          = KidsMint,
    onPrimary        = Color.White,
    primaryContainer = KidsMintLight,
    secondary        = KidsYellow,
    onSecondary      = KidsTextPrimary,
    background       = KidsBg,
    surface          = KidsCardBg,
    onBackground     = KidsTextPrimary,
    onSurface        = KidsTextPrimary,
    error            = KidsPink,
    onError          = Color.White
)

@Composable
fun SoyleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KidsColorScheme,
        typography  = KidsTypography,
        content     = content
    )
}
