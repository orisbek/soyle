package com.example.soyle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary          = Color(0xFF1F5FAD),   // синий — основной
    onPrimary        = Color.White,
    secondary        = Color(0xFF2E75B6),
    onSecondary      = Color.White,
    tertiary         = Color(0xFFFFC107),   // жёлтый — XP, достижения
    background       = Color(0xFFF5F9FF),
    surface          = Color.White,
    error            = Color(0xFFF44336)
)

@Composable
fun SoyleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content     = content
    )
}