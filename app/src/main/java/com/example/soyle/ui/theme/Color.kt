package com.example.soyle.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// ── Цвета приложения — var+mutableStateOf: Compose перерисует UI при смене темы

var SoyleBg          by mutableStateOf(Color(0xFF000000))
var SoyleSurface     by mutableStateOf(Color(0xFF111111))
var SoyleSurface2    by mutableStateOf(Color(0xFF1A1A1A))
var SoyleSurface3    by mutableStateOf(Color(0xFF242424))
var SoyleBorder      by mutableStateOf(Color(0xFF2A2A2A))
var SoyleBorderLight by mutableStateOf(Color(0xFF3A3A3A))

var SoyleTextPrimary   by mutableStateOf(Color(0xFFFFFFFF))
var SoyleTextSecondary by mutableStateOf(Color(0xFF888888))
var SoyleTextMuted     by mutableStateOf(Color(0xFF555555))
var SoyleTextDisabled  by mutableStateOf(Color(0xFF333333))

// Акценты — не меняются со сменой темы
val SoyleAccent      = Color(0xFF7C5DE8)
val SoyleAccentLight = Color(0xFF9B7FF4)
val SoyleAccentSoft  = Color(0xFF2D1F5E)

val SoyleGreen     = Color(0xFF4ADE80)
val SoyleGreenSoft = Color(0xFF14532D)
val SoyleAmber     = Color(0xFFFBBF24)
val SoyleAmberSoft = Color(0xFF451A03)
val SoyleRed       = Color(0xFFFF6B6B)
val SoyleRedSoft   = Color(0xFF4C0519)
val SoyleStreak    = Color(0xFFFF6B35)

var SoyleButtonPrimary     by mutableStateOf(Color(0xFFE8E8E8))
var SoyleButtonPrimaryText by mutableStateOf(Color(0xFF111111))
val SoyleButtonDark        = Color(0xFF1E1E1E)

fun speechScoreColor(score: Int): Color = when {
    score >= 85 -> Color(0xFF4ADE80)
    score >= 65 -> Color(0xFFFBBF24)
    score >= 40 -> Color(0xFFFF9F43)
    else        -> Color(0xFFFF6B6B)
}
