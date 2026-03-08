package com.example.soyle.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreBar(
    score    : Int,           // 0–100
    modifier : Modifier = Modifier
) {
    // Анимация: 0 → score за 1 секунду
    var targetScore by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(score) { targetScore = score.toFloat() }

    val animatedScore by animateFloatAsState(
        targetValue   = targetScore,
        animationSpec = tween(durationMillis = 1000),
        label         = "scoreAnim"
    )

    val color = when {
        score >= 85 -> Color(0xFF4CAF50)   // зелёный
        score >= 65 -> Color(0xFFFFC107)   // жёлтый
        score >= 45 -> Color(0xFFFF9800)   // оранжевый
        else        -> Color(0xFFF44336)   // красный
    }

    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress      = { animatedScore / 100f },
                modifier      = Modifier.size(140.dp),
                color         = color,
                trackColor    = color.copy(alpha = 0.15f),
                strokeWidth   = 10.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = "${animatedScore.toInt()}%",
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = color
                )
                Text(
                    text     = scoreLabel(score),
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun scoreLabel(score: Int) = when {
    score >= 85 -> "Отлично! 🎉"
    score >= 65 -> "Хорошо! 😊"
    score >= 45 -> "Старайся 🙂"
    else        -> "Попробуй ещё 💪"
}