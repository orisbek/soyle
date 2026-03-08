package com.example.soyle.ui.theme

import androidx.compose.ui.graphics.Color

// ── Основная палитра (Duolingo-style) ─────────────────────────────────────────
val DuoGreen         = Color(0xFF58CC02)   // основной зелёный — кнопки, акценты
val DuoGreenDark     = Color(0xFF46A302)   // тень под кнопкой
val DuoGreenLight    = Color(0xFFD7FFB8)   // светлый фон выделения
val DuoGreenPale     = Color(0xFFF0FFF0)   // очень светлый фон

val DuoBlue          = Color(0xFF1CB0F6)   // вторичный — выделение выбранного
val DuoBlueDark      = Color(0xFF0A91D5)
val DuoBlueLight     = Color(0xFFDDF4FF)   // фон выбранной карточки

val DuoRed           = Color(0xFFFF4B4B)   // ошибки, жизни
val DuoRedLight      = Color(0xFFFFE0E0)

val DuoYellow        = Color(0xFFFFC800)   // streak, XP, достижения
val DuoYellowLight   = Color(0xFFFFF3CC)
val DuoOrange        = Color(0xFFFF9600)   // предупреждение

val DuoPurple        = Color(0xFFCE82FF)   // редкие акценты

// ── Нейтральные ───────────────────────────────────────────────────────────────
val DuoWhite         = Color(0xFFFFFFFF)
val DuoBg            = Color(0xFFFFFFFF)   // фон экранов — чисто белый
val DuoCardBg        = Color(0xFFFFFFFF)

val DuoBorder        = Color(0xFFE5E5E5)   // граница карточек
val DuoBorderSelected = Color(0xFF84D8FF)  // граница выбранной карточки

val DuoGray          = Color(0xFFAFB7C3)   // неактивный текст
val DuoGrayLight     = Color(0xFFE5E5E5)   // неактивная кнопка фон
val DuoGrayDark      = Color(0xFF777777)

val DuoTextPrimary   = Color(0xFF3C3C3C)   // основной текст
val DuoTextSecondary = Color(0xFF777777)
val DuoTextDisabled  = Color(0xFFAFB7C3)

// ── Прогресс-бар ──────────────────────────────────────────────────────────────
val DuoProgressBg    = Color(0xFFE5E5E5)
val DuoProgressFill  = DuoGreen

// ── Score цвета ───────────────────────────────────────────────────────────────
fun scoreColor(score: Int): Color = when {
    score >= 85 -> DuoGreen
    score >= 65 -> DuoYellow
    score >= 45 -> DuoOrange
    else        -> DuoRed
}

fun levelColor(level: Int): Color = when {
    level >= 20 -> DuoPurple
    level >= 15 -> DuoBlue
    level >= 10 -> DuoYellow
    level >= 5  -> DuoGreen
    else        -> DuoGray
}