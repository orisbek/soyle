package com.example.soyle.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.domain.model.MascotEmotion

@Composable
fun MascotView(
    emotion  : MascotEmotion,
    modifier : Modifier = Modifier
) {
    val emoji = when (emotion) {
        MascotEmotion.HAPPY       -> "🦉😄"
        MascotEmotion.GOOD        -> "🦉😊"
        MascotEmotion.NEUTRAL     -> "🦉🙂"
        MascotEmotion.SUPPORTIVE  -> "🦉💪"
        MascotEmotion.CELEBRATING -> "🦉🎉"
        MascotEmotion.GREETING    -> "🦉👋"
    }

    val phrase = when (emotion) {
        MascotEmotion.HAPPY       -> "Вау! Просто супер!"
        MascotEmotion.GOOD        -> "Хорошо! Ещё чуть-чуть!"
        MascotEmotion.NEUTRAL     -> "Неплохо, попробуй ещё!"
        MascotEmotion.SUPPORTIVE  -> "Не сдавайся, у тебя получится!"
        MascotEmotion.CELEBRATING -> "Новый рекорд! Ты лучший!"
        MascotEmotion.GREETING    -> "Привет! Пора тренироваться!"
    }

    // Прыжок при HAPPY и CELEBRATING
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val jumpScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = if (emotion == MascotEmotion.HAPPY ||
            emotion == MascotEmotion.CELEBRATING) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jumpScale"
    )

    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text     = emoji,
            fontSize = 64.sp,
            modifier = Modifier.scale(jumpScale)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text      = phrase,
            fontSize  = 16.sp,
            textAlign = TextAlign.Center,
            color     = androidx.compose.ui.graphics.Color(0xFF555555)
        )
    }
}