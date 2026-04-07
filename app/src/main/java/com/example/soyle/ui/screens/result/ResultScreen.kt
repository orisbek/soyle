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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.theme.*

@Composable
fun ResultScreen(
    score   : Int,
    phoneme : String,
    onRetry : () -> Unit,
    onHome  : () -> Unit
) {
    val stars = when {
        score >= 85 -> 3
        score >= 60 -> 2
        score >= 40 -> 1
        else        -> 0
    }
    val (mascotEmoji, mascotText) = when {
        score >= 85 -> "🎉" to "Потрясающе! Ты настоящий мастер! 🏆"
        score >= 65 -> "🥳" to "Хорошо! Ещё немного и будет отлично! 💪"
        score >= 45 -> "😊" to "Неплохо! Продолжай стараться! 🙂"
        else        -> "💪" to "Не сдавайся! Попробуй ещё раз!"
    }
    val xpEarned = when {
        score >= 85 -> 20
        score >= 65 -> 15
        score >= 45 -> 10
        else        -> 5
    }
    val gaugeColor = scoreColor(score)

    // Анимация счёта
    val animatedScore by animateIntAsState(
        targetValue   = score,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label         = "scoreAnim"
    )

    // Анимация звёзд
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starRotate by infiniteTransition.animateFloat(
        initialValue  = -5f,
        targetValue   = 5f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starRot"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidsBg)
    ) {
        // ── Шапка ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(gaugeColor.copy(alpha = 0.8f), gaugeColor)
                    )
                )
                .padding(20.dp)
        ) {
            Text(
                text       = "Результат",
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                modifier   = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier            = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // ── Маскот ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(KidsMintLight, KidsBlueLight)
                        )
                    )
                    .border(3.dp, KidsMint.copy(0.4f), RoundedCornerShape(24.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(mascotEmoji, fontSize = 44.sp)
                Text(
                    text       = mascotText,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = KidsTextPrimary,
                    lineHeight = 22.sp,
                    modifier   = Modifier.weight(1f)
                )
            }

            // ── Круг с результатом ────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    gaugeColor.copy(alpha = 0.2f),
                                    gaugeColor.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(6.dp, gaugeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = "$animatedScore%",
                            fontSize   = 44.sp,
                            fontWeight = FontWeight.Black,
                            color      = gaugeColor
                        )
                        Text(
                            text       = "Звук «$phoneme»",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color      = KidsTextSecondary
                        )
                    }
                }

                // Звёзды
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { i ->
                        Text(
                            text     = if (i < stars) "⭐" else "☆",
                            fontSize = 36.sp,
                            modifier = if (i < stars) Modifier.rotate(starRotate * (i - 1)) else Modifier
                        )
                    }
                }

                // XP
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(KidsYellowLight)
                        .border(2.dp, KidsYellow, RoundedCornerShape(20.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⭐", fontSize = 20.sp)
                        Text(
                            text       = "+$xpEarned XP",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = KidsYellowDark
                        )
                    }
                }
            }
        }

        // ── Кнопки ────────────────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(listOf(KidsMint, KidsBlue))
                    )
                    .clickable(onClick = onHome)
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "🏠  ДОМОЙ",
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(3.dp, KidsMint, RoundedCornerShape(24.dp))
                    .clickable(onClick = onRetry)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "🔄  Попробовать снова",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = KidsMint
                )
            }
        }
    }
}
