package com.example.soyle.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

@Composable
fun ResultScreen(
    score   : Int,
    phoneme : String,
    onRetry : () -> Unit,
    onHome  : () -> Unit
) {
    val mascotText = when {
        score >= 85 -> "Потрясающе! Ты настоящий мастер! 🏆"
        score >= 65 -> "Хорошо! Ещё немного и будет отлично! 💪"
        score >= 45 -> "Неплохо! Продолжай стараться! 🙂"
        else        -> "Не сдавайся! Попробуй ещё раз! 🦉"
    }
    val xpEarned = when {
        score >= 85 -> 20
        score >= 65 -> 15
        score >= 45 -> 10
        else        -> 5
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(DuoWhite)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ── Маскот ────────────────────────────────────────────────────────
        DuoMascotSpeech(text = mascotText)

        // ── Результат ─────────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text       = "Результат",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 22.sp,
                color      = DuoTextPrimary
            )

            DuoScoreCircle(score = score, size = 160.dp)

            Text(
                text     = "Звук «$phoneme»",
                fontSize = 16.sp,
                color    = DuoTextSecondary
            )

            DuoXpBadge(xp = xpEarned)
        }

        // ── Кнопки ────────────────────────────────────────────────────────
        Column(
            modifier            = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DuoButton(
                text     = "ПРОДОЛЖИТЬ",
                modifier = Modifier.fillMaxWidth(),
                onClick  = onHome
            )
            DuoOutlineButton(
                text     = "Попробовать снова",
                modifier = Modifier.fillMaxWidth(),
                onClick  = onRetry
            )
        }
    }
}