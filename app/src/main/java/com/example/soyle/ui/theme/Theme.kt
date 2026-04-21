package com.example.soyle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),
    secondary = Color(0xFFFFC107),
    tertiary = Color(0xFF03A9F4),
    background = Color(0xFFFFF9C4),
    surface = Color(0xFFFFFFFF)
)

@Composable
fun SoyleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}