package com.example.soyle.ui.theme

import androidx.compose.ui.graphics.Color

// ── Детская палитра «Söyle Kids» ─────────────────────────────────────────────
val KidsMint          = Color(0xFF4ECDC4)
val KidsMintDark      = Color(0xFF38B2A9)
val KidsMintLight     = Color(0xFFB2F0ED)

val KidsYellow        = Color(0xFFFFD93D)
val KidsYellowDark    = Color(0xFFF0C030)
val KidsYellowLight   = Color(0xFFFFF6C2)

val KidsPink          = Color(0xFFFF6B9D)
val KidsPinkLight     = Color(0xFFFFE0EE)

val KidsOrange        = Color(0xFFFF9F43)
val KidsPurple        = Color(0xFFA29BFE)
val KidsPurpleDark    = Color(0xFF7B73F0)

val KidsBlue          = Color(0xFF54A0FF)
val KidsBlueLight     = Color(0xFFD6EAFF)

val KidsGreen         = Color(0xFF6BCB77)
val KidsGreenDark     = Color(0xFF4EB05A)
val KidsGreenLight    = Color(0xFFCEF2D1)

val KidsRed           = Color(0xFFFF6B6B)
val KidsBg            = Color(0xFFFFF8F0)
val KidsCardBg        = Color(0xFFFFFFFF)
val KidsBorder        = Color(0xFFEEE0D5)
val KidsTextPrimary   = Color(0xFF2D3436)
val KidsTextSecondary = Color(0xFF888888)
val KidsTextDisabled  = Color(0xFFBBBBBB)
val KidsStar          = Color(0xFFFFD700)

// Алиасы для обратной совместимости
val DuoGreen          = KidsMint
val DuoGreenDark      = KidsMintDark
val DuoGreenLight     = KidsMintLight
val DuoGreenPale      = Color(0xFFF0FFFE)
val DuoBlue           = KidsBlue
val DuoBlueDark       = Color(0xFF3D8FEF)
val DuoBlueLight      = KidsBlueLight
val DuoRed            = KidsPink
val DuoRedLight       = KidsPinkLight
val DuoYellow         = KidsYellow
val DuoYellowLight    = KidsYellowLight
val DuoOrange         = KidsOrange
val DuoPurple         = KidsPurple
val DuoWhite          = Color(0xFFFFFFFF)
val DuoBg             = KidsBg
val DuoCardBg         = KidsCardBg
val DuoBorder         = KidsBorder
val DuoBorderSelected = KidsMint
val DuoGray           = Color(0xFFAAB2C0)
val DuoGrayLight      = Color(0xFFEEEEEE)
val DuoGrayDark       = Color(0xFF666666)
val DuoTextPrimary    = KidsTextPrimary
val DuoTextSecondary  = KidsTextSecondary
val DuoTextDisabled   = KidsTextDisabled
val DuoProgressBg     = Color(0xFFE5E5E5)
val DuoProgressFill   = KidsMint

fun scoreColor(score: Int): Color = when {
    score >= 85 -> KidsGreen
    score >= 65 -> KidsYellow
    score >= 45 -> KidsOrange
    else        -> KidsPink
}

fun levelColor(level: Int): Color = when {
    level >= 20 -> KidsPurple
    level >= 15 -> KidsBlue
    level >= 10 -> KidsYellow
    level >= 5  -> KidsMint
    else        -> Color(0xFFAAB2C0)
}
