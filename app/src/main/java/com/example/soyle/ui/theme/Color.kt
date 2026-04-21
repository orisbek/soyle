package com.example.soyle.ui.theme

import androidx.compose.ui.graphics.Color

// ── Основная палитра (тёмная, минималистичная) ────────────────────────────────
val SoyleBg          = Color(0xFF000000)   // Чистый чёрный фон
val SoyleSurface     = Color(0xFF111111)   // Поверхность карточек
val SoyleSurface2    = Color(0xFF1A1A1A)   // Вторичная поверхность
val SoyleSurface3    = Color(0xFF242424)   // Третичная (активные элементы)
val SoyleBorder      = Color(0xFF2A2A2A)   // Границы
val SoyleBorderLight = Color(0xFF3A3A3A)   // Лёгкие границы

// ── Текст ─────────────────────────────────────────────────────────────────────
val SoyleTextPrimary   = Color(0xFFFFFFFF)
val SoyleTextSecondary = Color(0xFF888888)
val SoyleTextMuted     = Color(0xFF555555)
val SoyleTextDisabled  = Color(0xFF333333)

// ── Акценты ───────────────────────────────────────────────────────────────────
val SoyleAccent       = Color(0xFF7C5DE8)   // Фиолетовый (как в Stoic)
val SoyleAccentLight  = Color(0xFF9B7FF4)
val SoyleAccentSoft   = Color(0xFF2D1F5E)   // Мягкий фон акцента

val SoyleGreen        = Color(0xFF4ADE80)
val SoyleGreenSoft    = Color(0xFF14532D)
val SoyleAmber        = Color(0xFFFBBF24)
val SoyleAmberSoft    = Color(0xFF451A03)
val SoyleRed          = Color(0xFFFF6B6B)
val SoyleRedSoft      = Color(0xFF4C0519)

// ── Кнопка ────────────────────────────────────────────────────────────────────
val SoyleButtonPrimary   = Color(0xFFE8E8E8)   // Светлая пилюля (как в Stoic)
val SoyleButtonPrimaryText = Color(0xFF111111)
val SoyleButtonDark      = Color(0xFF1E1E1E)

// ── Streak/Fire ───────────────────────────────────────────────────────────────
val SoyleStreak = Color(0xFFFF6B35)

fun speechScoreColor(score: Int): Color = when {
    score >= 85 -> Color(0xFF4ADE80)
    score >= 65 -> Color(0xFFFBBF24)
    score >= 40 -> Color(0xFFFF9F43)
    else        -> Color(0xFFFF6B6B)
}