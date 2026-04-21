package com.example.soyle.ui.screens.result

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Экран результата (как Stoic — серия + достижение) ────────────────────────

@Composable
fun ResultScreen(
    score   : Int = 78,
    phoneme : String = "Р",
    onHome  : () -> Unit = {},
    onRetry : () -> Unit = {}
) {
    val stars = when { score >= 85 -> 3; score >= 60 -> 2; score >= 40 -> 1; else -> 0 }
    val xp    = when { score >= 85 -> 20; score >= 65 -> 15; score >= 45 -> 10; else -> 5 }
    val color = speechScoreColor(score)

    val infiniteTransition = rememberInfiniteTransition(label = "result")
    val rotateAnim by infiniteTransition.animateFloat(
        initialValue  = -3f,
        targetValue   = 3f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        // Кнопки сверху
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Поделиться
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(SoyleSurface),
                contentAlignment = Alignment.Center
            ) { Text("↑", fontSize = 16.sp, color = SoyleTextSecondary) }
            // Звезда (в избранное)
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(SoyleSurface),
                contentAlignment = Alignment.Center
            ) { Text("☆", fontSize = 16.sp, color = SoyleTextSecondary) }
            // Закрыть
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(SoyleSurface).clickable(onClick = onHome),
                contentAlignment = Alignment.Center
            ) { Text("×", fontSize = 20.sp, color = SoyleTextSecondary) }
        }

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(60.dp))

            // Цветочек (как в Stoic — streak flower)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .rotate(rotateAnim),
                contentAlignment = Alignment.Center
            ) {
                Text("✿", fontSize = 64.sp, color = SoyleTextMuted.copy(alpha = 0.4f))
            }

            Spacer(Modifier.height(24.dp))

            // Заголовок
            Text(
                text       = "1-day streak.",
                fontWeight = FontWeight.Bold,
                fontSize   = 28.sp,
                color      = SoyleTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = when {
                    score >= 85 -> "Отличное произношение! Первый шаг сделан!"
                    score >= 65 -> "Хорошая работа! Продолжай так держать!"
                    else        -> "Внутренняя работа — это путь. Ты сделал первый шаг!"
                },
                fontSize  = 16.sp,
                color     = SoyleTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(36.dp))

            // Мини-серия (как в Stoic — Mon Tue Wed)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                StreakDot(day = "Пн", isFilled = true)
                StreakDot(day = "Вт", isFilled = false)
                StreakDot(day = "Ср", isFilled = false)
            }

            Spacer(Modifier.height(40.dp))

            // Результат
            CircularScore(score = score, size = 110.dp)

            Spacer(Modifier.height(16.dp))

            // Звёзды
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Text(
                        text     = if (i < stars) "★" else "☆",
                        fontSize = 28.sp,
                        color    = if (i < stars) SoyleAmber else SoyleTextMuted
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // XP
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("⭐", fontSize = 16.sp)
                Text("+$xp XP", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SoyleAmber)
            }

            Spacer(Modifier.weight(1f))
        }

        // Кнопка внизу
        SoylePrimaryButton(
            text     = "Здорово!",
            onClick  = onHome,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 40.dp)
        )
    }
}

@Composable
private fun StreakDot(day: String, isFilled: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isFilled) SoyleButtonPrimary else SoyleSurface)
                .border(1.dp, if (isFilled) Color.Transparent else SoyleBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isFilled) Text("🔥", fontSize = 16.sp)
        }
        Text(day, fontSize = 11.sp, color = SoyleTextMuted)
    }
}