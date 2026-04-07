package com.example.soyle.ui.screens.pronunciation

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.foundation.Canvas
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import com.example.soyle.ui.theme.*

/**
 * 🎯 Экран «Точность произношения»
 *
 * Показывает:
 * 1. Большой круговой индикатор точности (0–100%)
 * 2. Детальный разбор — какие ошибки допущены
 * 3. Советы логопеда — как исправить
 * 4. История последних попыток
 * 5. Анимированные звуковые волны при записи
 */

data class PronunciationAttempt(
    val expected  : String,
    val actual    : String,
    val score     : Int,
    val feedback  : String,
    val timestamp : Long = System.currentTimeMillis()
)

@Composable
fun PronunciationAccuracyScreen(
    attempts  : List<PronunciationAttempt> = emptyList(),
    isRecording: Boolean = false,
    onRecord  : () -> Unit = {},
    onBack    : () -> Unit = {}
) {
    val lastAttempt = attempts.lastOrNull()
    val avgScore    = if (attempts.isEmpty()) 0 else attempts.sumOf { it.score } / attempts.size
    val bestScore   = attempts.maxOfOrNull { it.score } ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidsBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Шапка ─────────────────────────────────────────────────────────────
        AccuracyTopBar(onBack = onBack)

        Column(
            modifier            = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Главный индикатор точности ─────────────────────────────────
            MainAccuracyGauge(
                score      = lastAttempt?.score ?: 0,
                isRecording = isRecording,
                onRecord   = onRecord
            )

            // ── Разбор произношения ────────────────────────────────────────
            if (lastAttempt != null) {
                PronunciationBreakdown(attempt = lastAttempt)
            }

            // ── Советы ────────────────────────────────────────────────────
            if (lastAttempt != null) {
                SpeechTips(attempt = lastAttempt)
            }

            // ── Статистика ────────────────────────────────────────────────
            if (attempts.size >= 2) {
                StatsPanel(
                    avgScore   = avgScore,
                    bestScore  = bestScore,
                    totalTries = attempts.size
                )
            }

            // ── История попыток ───────────────────────────────────────────
            if (attempts.isNotEmpty()) {
                AttemptsHistory(attempts = attempts.takeLast(5).reversed())
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Главный круговой индикатор ────────────────────────────────────────────────

@Composable
private fun MainAccuracyGauge(
    score      : Int,
    isRecording : Boolean,
    onRecord   : () -> Unit
) {
    val animatedScore by animateIntAsState(
        targetValue  = score,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label        = "scoreAnim"
    )

    val sweepAngle = (animatedScore / 100f) * 360f
    val gaugeColor = when {
        score >= 85 -> KidsGreen
        score >= 65 -> KidsYellow
        score >= 45 -> KidsOrange
        else        -> KidsPink
    }

    // Пульсирующий эффект при записи
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Заголовок
        Text(
            text       = "🎯 Точность произношения",
            fontSize   = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = KidsTextPrimary
        )

        // Круговой индикатор
        Box(
            modifier         = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 20.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)

                // Фоновый круг
                drawArc(
                    color      = Color(0xFFEEEEEE),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter  = false,
                    style      = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft    = androidx.compose.ui.geometry.Offset(
                        center.x - radius, center.y - radius
                    ),
                    size       = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )

                // Цветная дуга
                drawArc(
                    brush      = Brush.sweepGradient(
                        colors = listOf(
                            gaugeColor.copy(alpha = 0.3f),
                            gaugeColor,
                            gaugeColor
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter  = false,
                    style      = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft    = androidx.compose.ui.geometry.Offset(
                        center.x - radius, center.y - radius
                    ),
                    size       = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
            }

            // Центральное содержимое
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text       = "$animatedScore%",
                    fontSize   = 44.sp,
                    fontWeight = FontWeight.Black,
                    color      = gaugeColor
                )
                Text(
                    text       = accuracyLabel(score),
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = KidsTextSecondary
                )
            }
        }

        // Кнопка записи
        Box(
            modifier = Modifier
                .scale(if (isRecording) pulseScale else 1f)
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isRecording)
                        Brush.radialGradient(colors = listOf(KidsPink, Color(0xFFCC0044)))
                    else
                        Brush.radialGradient(colors = listOf(KidsMint, KidsMintDark))
                )
                .border(4.dp, if (isRecording) KidsPink.copy(0.4f) else KidsMint.copy(0.3f), CircleShape)
                .clickable(onClick = onRecord),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = if (isRecording) "⏹" else "🎙",
                fontSize = 32.sp
            )
        }

        Text(
            text       = if (isRecording) "Говори сейчас..." else "Нажми чтобы записать",
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = KidsTextSecondary
        )
    }
}

// ── Разбор произношения ───────────────────────────────────────────────────────

@Composable
private fun PronunciationBreakdown(attempt: PronunciationAttempt) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(3.dp, KidsBorder, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text       = "📋 Разбор",
            fontSize   = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = KidsTextPrimary
        )

        // Сравнение: ожидалось vs сказано
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ожидалось
            ComparisonBox(
                label   = "Нужно было сказать",
                text    = attempt.expected,
                color   = KidsMint,
                bgColor = KidsMintLight,
                modifier = Modifier.weight(1f)
            )
            // Сказано
            ComparisonBox(
                label   = "Ты сказал",
                text    = attempt.actual.ifEmpty { "—" },
                color   = if (attempt.score >= 80) KidsGreen else KidsPink,
                bgColor = if (attempt.score >= 80) KidsGreenLight else KidsPinkLight,
                modifier = Modifier.weight(1f)
            )
        }

        // Обратная связь
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (attempt.score >= 80) KidsGreenLight else KidsYellowLight
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment     = Alignment.Top
        ) {
            Text(
                text     = if (attempt.score >= 80) "🦜" else "💡",
                fontSize = 24.sp
            )
            Text(
                text       = attempt.feedback,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = KidsTextPrimary,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun ComparisonBox(
    label   : String,
    text    : String,
    color   : Color,
    bgColor : Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(2.dp, color.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            color      = KidsTextSecondary,
            textAlign  = TextAlign.Center
        )
        Text(
            text       = text,
            fontSize   = 24.sp,
            fontWeight = FontWeight.Black,
            color      = color,
            textAlign  = TextAlign.Center
        )
    }
}

// ── Советы логопеда ───────────────────────────────────────────────────────────

@Composable
private fun SpeechTips(attempt: PronunciationAttempt) {
    val tips = generateTips(attempt)
    if (tips.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFF8F0))
                )
            )
            .border(3.dp, KidsOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🧠", fontSize = 22.sp)
            Text(
                text       = "Советы логопеда",
                fontSize   = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = KidsTextPrimary
            )
        }

        tips.forEach { tip ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(KidsOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Black)
                }
                Text(
                    text       = tip,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color      = KidsTextPrimary,
                    lineHeight = 21.sp,
                    modifier   = Modifier.weight(1f)
                )
            }
        }
    }
}

// ── Панель статистики ─────────────────────────────────────────────────────────

@Composable
private fun StatsPanel(avgScore: Int, bestScore: Int, totalTries: Int) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            emoji  = "📈",
            label  = "Среднее",
            value  = "$avgScore%",
            color  = KidsBlue,
            bgColor = KidsBlueLight,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            emoji  = "🏆",
            label  = "Лучшее",
            value  = "$bestScore%",
            color  = KidsYellow,
            bgColor = KidsYellowLight,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            emoji  = "🔄",
            label  = "Попыток",
            value  = totalTries.toString(),
            color  = KidsPurple,
            bgColor = Color(0xFFEDE7FF),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    emoji  : String,
    label  : String,
    value  : String,
    color  : Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(2.dp, color.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(
            text       = value,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Black,
            color      = color
        )
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            color      = KidsTextSecondary,
            textAlign  = TextAlign.Center
        )
    }
}

// ── История попыток ───────────────────────────────────────────────────────────

@Composable
private fun AttemptsHistory(attempts: List<PronunciationAttempt>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(3.dp, KidsBorder, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text       = "📜 Последние попытки",
            fontSize   = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = KidsTextPrimary
        )

        attempts.forEachIndexed { index, attempt ->
            AttemptRow(attempt = attempt, index = index)
            if (index < attempts.size - 1) {
                HorizontalDivider(color = KidsBorder, thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun AttemptRow(attempt: PronunciationAttempt, index: Int) {
    val color = scoreColor(attempt.score)
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text     = if (index == 0) "🆕" else "${index + 1}.",
                fontSize = 16.sp
            )
            Column {
                Text(
                    text       = "«${attempt.expected}»",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = KidsTextPrimary
                )
                Text(
                    text       = attempt.feedback.take(35) + if (attempt.feedback.length > 35) "..." else "",
                    fontSize   = 11.sp,
                    color      = KidsTextSecondary
                )
            }
        }

        // Мини-бар точности
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(attempt.score / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
            Text(
                text       = "${attempt.score}%",
                fontSize   = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = color
            )
        }
    }
}

// ── Шапка экрана ─────────────────────────────────────────────────────────────

@Composable
private fun AccuracyTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(KidsPurple, KidsBlue)
                )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Text("←", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text       = "🎯 Точность звука",
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White
            )
        }
    }
}

// ── Вспомогательные функции ───────────────────────────────────────────────────

private fun accuracyLabel(score: Int) = when {
    score >= 85 -> "Отлично! 🏆"
    score >= 65 -> "Хорошо! 💪"
    score >= 45 -> "Неплохо! 🙂"
    score > 0   -> "Продолжай! 💡"
    else        -> "Нажми запись"
}

private fun generateTips(attempt: PronunciationAttempt): List<String> {
    val tips = mutableListOf<String>()
    val exp = attempt.expected.lowercase()
    val act = attempt.actual.lowercase()

    if (act.isEmpty()) {
        tips += "Нажми кнопку 🎙 и громко произнеси звук"
        return tips
    }
    if (exp.contains("р") && act.contains("л") && !act.contains("р")) {
        tips += "Подними кончик языка к бугоркам за верхними зубами"
        tips += "Попробуй порычать как тигр: «Р-р-р-р!»"
        tips += "Заставь кончик языка вибрировать — подуй на него"
    }
    if (attempt.score < 50) {
        tips += "Произноси медленно — сначала отдельный звук, потом слог, потом слово"
        tips += "Посмотри в зеркало — следи за положением языка"
    } else if (attempt.score < 80) {
        tips += "Почти правильно! Повтори ещё 2-3 раза для закрепления"
    }
    if (tips.isEmpty() && attempt.score >= 80) {
        tips += "Молодец! Ты отлично произносишь этот звук 🌟"
        tips += "Теперь попробуй более сложные слова с буквой Р"
    }
    return tips
}
