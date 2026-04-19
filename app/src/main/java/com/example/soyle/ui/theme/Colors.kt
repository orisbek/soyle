package com.example.soyle.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF2E7D32)
val PrimaryDark = Color(0xFF1B5E20)
val Accent = Color(0xFF1565C0)
val Background = Color(0xFFF7F9FC)
val Surface = Color(0xFFFFFFFF)
val Border = Color(0xFFDDE3EC)
val TextPrimary = Color(0xFF1F2937)
val TextSecondary = Color(0xFF6B7280)
val Error = Color(0xFFB3261E)

fun scoreColor(score: Int): Color = when {
    score >= 85 -> Primary
    score >= 65 -> Accent
    else -> Error
}

fun levelColor(level: Int): Color = when {
    level >= 10 -> Accent
    level >= 5 -> Primary
    else -> TextSecondary
}

// Backward-compatible aliases
val DuoGreen = Primary
val DuoGreenDark = PrimaryDark
val DuoGreenLight = Primary
val DuoGreenPale = Background
val DuoBlue = Accent
val DuoBlueDark = Accent
val DuoBlueLight = Background
val DuoRed = Error
val DuoRedLight = Error.copy(alpha = 0.15f)
val DuoYellow = Accent
val DuoYellowLight = Background
val DuoOrange = Accent
val DuoPurple = Accent
val DuoWhite = Surface
val DuoBg = Background
val DuoCardBg = Surface
val DuoBorder = Border
val DuoBorderSelected = Accent
val DuoGray = TextSecondary
val DuoGrayLight = Border
val DuoGrayDark = TextSecondary
val DuoTextPrimary = TextPrimary
val DuoTextSecondary = TextSecondary
val DuoTextDisabled = TextSecondary
val DuoProgressBg = Border
val DuoProgressFill = Primary
