package com.example.soyle.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import com.example.soyle.ui.theme.*
import kotlinx.coroutines.delay

// ── Экран «Где буква Р?» ──────────────────────────────────────────────────────

@Composable
fun WhereIsLetterScreen(onBack: () -> Unit = {}) {
    // Слова перемешиваем один раз
    val words = remember { WhereRData.forLevel(3) }   // все уровни вместе
    var lives      by remember { mutableIntStateOf(3) }
    var score      by remember { mutableIntStateOf(0) }
    var currentIdx by remember { mutableIntStateOf(0) }
    var selected   by remember { mutableStateOf<RPosition?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var gameOver   by remember { mutableStateOf(false) }

    val total = words.size
    val current = if (currentIdx < total) words[currentIdx] else null

    // Обработка ответа
    fun onAnswer(pos: RPosition) {
        if (selected != null || current == null) return
        selected   = pos
        showResult = true
        if (pos == current.position) score++ else lives--
    }

    // Автопереход
    LaunchedEffect(showResult) {
        if (!showResult) return@LaunchedEffect
        delay(1100)
        if (lives <= 0 || currentIdx + 1 >= total) {
            gameOver = true
        } else {
            currentIdx++
            selected   = null
            showResult = false
        }
    }

    fun restart() {
        lives      = 3
        score      = 0
        currentIdx = 0
        selected   = null
        showResult = false
        gameOver   = false
    }

    when {
        gameOver -> GameOverScreen(
            score     = score,
            onBack    = onBack,
            onRestart = ::restart
        )
        current == null -> Box(Modifier.fillMaxSize().background(SoyleBg), Alignment.Center) {
            Text("Нет слов", color = SoyleTextSecondary)
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoyleBg)
            ) {
                GameTopBar(
                    title  = "Где буква «Р»?",
                    lives  = lives,
                    score  = score,
                    onBack = onBack
                )

                LinearProgressIndicator(
                    progress   = { (currentIdx + 1).toFloat() / total },
                    modifier   = Modifier.fillMaxWidth().height(3.dp),
                    color      = SoyleAccent,
                    trackColor = SoyleSurface2
                )

                Spacer(Modifier.weight(1f))

                // Инструкция
                Text(
                    text      = "Где стоит буква «Р»?",
                    fontSize  = 14.sp,
                    color     = SoyleTextSecondary,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                // Слово с подсветкой Р
                WordDisplay(
                    word       = current.word,
                    showResult = showResult,
                    position   = current.position
                )

                Spacer(Modifier.height(40.dp))

                // Кнопки выбора позиции
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val positions = listOf(
                        RPosition.BEGINNING to "Начало",
                        RPosition.MIDDLE    to "Середина",
                        RPosition.END       to "Конец"
                    )
                    positions.forEach { (pos, label) ->
                        PositionButton(
                            label      = label,
                            isCorrect  = pos == current.position,
                            isSelected = selected == pos,
                            showResult = showResult,
                            onClick    = { onAnswer(pos) },
                            modifier   = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text      = "${currentIdx + 1} / $total",
                    fontSize  = 12.sp,
                    color     = SoyleTextMuted,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ── Отображение слова с подсветкой ────────────────────────────────────────────

@Composable
private fun WordDisplay(word: String, showResult: Boolean, position: RPosition) {
    val rIndex = word.indexOfFirst { it.lowercaseChar() == 'р' }

    // Построить аннотированный текст: буква Р — акцентным цветом
    val annotated = buildAnnotatedString {
        word.forEachIndexed { i, ch ->
            if (i == rIndex) {
                withStyle(SpanStyle(color = SoyleAccent, fontWeight = FontWeight.ExtraBold)) {
                    append(ch.uppercaseChar().toString())
                }
            } else {
                withStyle(SpanStyle(color = SoyleTextPrimary)) {
                    append(ch.toString())
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SoyleSurface)
                .border(1.5.dp, SoyleBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 36.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text      = annotated,
                fontSize  = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

// ── Кнопка позиции ────────────────────────────────────────────────────────────

@Composable
private fun PositionButton(
    label      : String,
    isCorrect  : Boolean,
    isSelected : Boolean,
    showResult : Boolean,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier
) {
    val bgColor = when {
        showResult && isCorrect  -> Color(0xFF4CAF50).copy(alpha = 0.15f)
        showResult && isSelected -> Color(0xFFE53935).copy(alpha = 0.15f)
        else                     -> SoyleSurface
    }
    val borderColor = when {
        showResult && isCorrect  -> Color(0xFF4CAF50)
        showResult && isSelected -> Color(0xFFE53935)
        else                     -> SoyleBorder
    }
    val textColor = when {
        showResult && isCorrect  -> Color(0xFF4CAF50)
        showResult && isSelected -> Color(0xFFE53935)
        else                     -> SoyleTextPrimary
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = !showResult, onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(4.dp)
    ) {
        // Иконка позиции
        val icon = when {
            showResult && isCorrect  -> Icons.Outlined.CheckCircle
            showResult && isSelected -> Icons.Outlined.Cancel
            else                     -> null
        }
        if (icon != null) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935),
                modifier           = Modifier.size(20.dp)
            )
        } else {
            // Маленькая схема «где буква»
            PositionDiagram(label = label)
        }
        Text(
            text      = label,
            fontSize  = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color     = textColor,
            textAlign = TextAlign.Center
        )
    }
}

// ── Схема позиции ─────────────────────────────────────────────────────────────

@Composable
private fun PositionDiagram(label: String) {
    Row(
        modifier              = Modifier.padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        val dots = 3
        repeat(dots) { i ->
            val isHighlighted = when (label) {
                "Начало"   -> i == 0
                "Середина" -> i == 1
                else       -> i == 2
            }
            Box(
                modifier = Modifier
                    .size(if (isHighlighted) 10.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (isHighlighted) SoyleAccent else SoyleSurface2)
            )
        }
    }
}
