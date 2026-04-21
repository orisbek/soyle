package com.example.soyle.ui.screens.exercise

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Состояния упражнения ──────────────────────────────────────────────────────

sealed class ExerciseState {
    object Idle      : ExerciseState()
    object Recording : ExerciseState()
    object Analyzing : ExerciseState()
    data class Result(val score: Int, val feedback: String) : ExerciseState()
}

// ── Экран упражнения ──────────────────────────────────────────────────────────

@Composable
fun ExerciseScreen(
    phoneme  : String = "Р",
    title    : String = "Звук «Р»",
    onBack   : () -> Unit = {},
    onResult : (Int) -> Unit = {}
) {
    var state      by remember { mutableStateOf<ExerciseState>(ExerciseState.Idle) }
    var attempts   by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        // ── Шапка ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Назад
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoyleSurface)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Text("‹", fontSize = 22.sp, color = SoyleTextSecondary, fontWeight = FontWeight.Bold)
            }

            Text(
                text       = title,
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = SoyleTextPrimary
            )

            // Жизни
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { Text("·", fontSize = 20.sp, color = if (it < 3 - (attempts / 3)) SoyleTextPrimary else SoyleSurface3) }
            }
        }

        // ── Центральный контент ────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            // Большой звук/буква
            AnimatedContent(targetState = state, label = "phonemeAnim") { st ->
                when (st) {
                    is ExerciseState.Idle -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Подсказка
                            Text(
                                text     = "Нажми кнопку и произнеси",
                                fontSize = 16.sp,
                                color    = SoyleTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            // Большая буква
                            Text(
                                text          = phoneme,
                                fontSize      = 120.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = SoyleTextPrimary,
                                letterSpacing = (-4).sp
                            )
                            // Примеры слов
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf("Р-рыба", "Р-рак", "Р-рот").forEach { word ->
                                    SoyleTag(text = word)
                                }
                            }
                        }
                    }
                    is ExerciseState.Recording -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Text("Слушаю...", fontSize = 20.sp, color = SoyleTextSecondary)
                            WaveformAnimation()
                        }
                    }
                    is ExerciseState.Analyzing -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color    = SoyleAccent,
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 3.dp
                            )
                            Text("Анализирую произношение...", fontSize = 15.sp, color = SoyleTextSecondary)
                        }
                    }
                    is ExerciseState.Result -> {
                        ResultContent(score = st.score, feedback = st.feedback)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Кнопка записи ─────────────────────────────────────────────
            RecordSection(
                state    = state,
                onRecord = {
                    when (state) {
                        is ExerciseState.Idle -> {
                            state = ExerciseState.Recording
                            // В реальном проекте: запускаем AudioRecorder
                        }
                        is ExerciseState.Recording -> {
                            state = ExerciseState.Analyzing
                            // Симуляция анализа
                        }
                        is ExerciseState.Result -> {
                            attempts++
                            state = ExerciseState.Idle
                        }
                        else -> {}
                    }
                }
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Анимация волны ────────────────────────────────────────────────────────────

@Composable
private fun WaveformAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val barCount = 9

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        repeat(barCount) { i ->
            val height by infiniteTransition.animateFloat(
                initialValue = 8f,
                targetValue  = 36f,
                animationSpec = infiniteRepeatable(
                    animation  = tween(300 + i * 70, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar$i"
            )
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(height.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SoyleTextPrimary.copy(alpha = 0.5f + (i % 3) * 0.17f))
            )
        }
    }
}

// ── Результат ─────────────────────────────────────────────────────────────────

@Composable
private fun ResultContent(score: Int, feedback: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CircularScore(score = score, size = 130.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SoyleSurface)
                .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text      = feedback,
                fontSize  = 15.sp,
                color     = SoyleTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // XP
        val xp = when { score >= 85 -> 20; score >= 65 -> 15; score >= 45 -> 10; else -> 5 }
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("⭐", fontSize = 16.sp)
            Text("+$xp XP", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SoyleAmber)
        }
    }
}

// ── Секция с кнопкой записи ───────────────────────────────────────────────────

@Composable
private fun RecordSection(state: ExerciseState, onRecord: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "recordPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = if (state is ExerciseState.Recording) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    when (state) {
                        is ExerciseState.Recording -> SoyleRed.copy(alpha = 0.15f)
                        is ExerciseState.Result    -> SoyleSurface
                        else                       -> SoyleButtonPrimary.copy(alpha = 0.12f)
                    }
                )
                .border(
                    2.dp,
                    when (state) {
                        is ExerciseState.Recording -> SoyleRed
                        is ExerciseState.Result    -> SoyleBorder
                        else                       -> SoyleButtonPrimary.copy(alpha = 0.4f)
                    },
                    CircleShape
                )
                .clickable(
                    enabled = state !is ExerciseState.Analyzing,
                    onClick = onRecord
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (state) {
                    is ExerciseState.Recording -> "⏹"
                    is ExerciseState.Result    -> "🔄"
                    is ExerciseState.Analyzing -> "⌛"
                    else                       -> "🎙"
                },
                fontSize = 28.sp
            )
        }

        Text(
            text = when (state) {
                is ExerciseState.Recording -> "Говори сейчас"
                is ExerciseState.Analyzing -> ""
                is ExerciseState.Result    -> "Ещё раз"
                else                       -> "Нажми и произнеси"
            },
            fontSize = 13.sp,
            color    = SoyleTextSecondary
        )
    }
}