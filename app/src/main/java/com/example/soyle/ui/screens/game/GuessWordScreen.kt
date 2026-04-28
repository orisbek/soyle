package com.example.soyle.ui.screens.game

import android.speech.tts.TextToSpeech
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.soyle.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

// ── Уровень игры ──────────────────────────────────────────────────────────────

private enum class GuessLevel { SELECT, LEVEL1, LEVEL2, LEVEL3 }

// ── Главный экран ─────────────────────────────────────────────────────────────

@Composable
fun GuessWordScreen(onBack: () -> Unit = {}) {
    var level by remember { mutableStateOf(GuessLevel.SELECT) }

    AnimatedContent(
        targetState    = level,
        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
        label          = "guessLevel"
    ) { current ->
        when (current) {
            GuessLevel.SELECT -> LevelSelectScreen(
                onBack     = onBack,
                onSelect   = { level = it }
            )
            GuessLevel.LEVEL1 -> GuessGameScreen(
                levelNum = 1,
                onBack   = { level = GuessLevel.SELECT }
            )
            GuessLevel.LEVEL2 -> GuessGameScreen(
                levelNum = 2,
                onBack   = { level = GuessLevel.SELECT }
            )
            GuessLevel.LEVEL3 -> GuessGameScreen(
                levelNum = 3,
                onBack   = { level = GuessLevel.SELECT }
            )
        }
    }
}

// ── Выбор уровня ──────────────────────────────────────────────────────────────

@Composable
private fun LevelSelectScreen(
    onBack   : () -> Unit,
    onSelect : (GuessLevel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        // Кнопка назад
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад", tint = SoyleTextPrimary)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text       = "Угадай слово",
            fontWeight = FontWeight.Bold,
            fontSize   = 28.sp,
            color      = SoyleTextPrimary
        )
        Text(
            text      = "Выбери уровень сложности",
            fontSize  = 14.sp,
            color     = SoyleTextSecondary,
            modifier  = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        LevelCard(
            number      = 1,
            title       = "Лёгкий",
            description = "Короткие слова — рак, рот, рог…",
            color       = Color(0xFF4CAF50),
            onClick     = { onSelect(GuessLevel.LEVEL1) }
        )
        Spacer(Modifier.height(14.dp))
        LevelCard(
            number      = 2,
            title       = "Средний",
            description = "Слова посложнее — радуга, морковь…",
            color       = SoyleAccent,
            onClick     = { onSelect(GuessLevel.LEVEL2) }
        )
        Spacer(Modifier.height(14.dp))
        LevelCard(
            number      = 3,
            title       = "Сложный",
            description = "Длинные слова — руководство, разговор…",
            color       = Color(0xFFE53935),
            onClick     = { onSelect(GuessLevel.LEVEL3) }
        )
    }
}

@Composable
private fun LevelCard(
    number      : Int,
    title       : String,
    description : String,
    color       : Color,
    onClick     : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = number.toString(),
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = color
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = SoyleTextPrimary)
            Text(text = description, fontSize = 12.sp, color = SoyleTextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
        Icon(
            imageVector        = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint               = SoyleTextMuted,
            modifier           = Modifier.size(20.dp)
        )
    }
}

// ── Игровой экран ─────────────────────────────────────────────────────────────

@Composable
private fun GuessGameScreen(levelNum: Int, onBack: () -> Unit) {
    val context = LocalContext.current

    // TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val t = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru", "RU")
            }
        }
        tts = t
        onDispose { t.shutdown() }
    }

    // Все слова уровня
    val allWords = remember(levelNum) { GuessWordData.forLevel(levelNum).toMutableList() }

    // Состояния игры
    var lives        by remember { mutableIntStateOf(3) }
    var score        by remember { mutableIntStateOf(0) }
    var currentIdx   by remember { mutableIntStateOf(0) }
    var options      by remember { mutableStateOf<List<String>>(emptyList()) }
    var selected     by remember { mutableStateOf<String?>(null) }
    var showResult   by remember { mutableStateOf(false) }
    var gameOver     by remember { mutableStateOf(false) }
    var gameWon      by remember { mutableStateOf(false) }

    // Анимация кнопки говорить
    val speakScale   by animateFloatAsState(
        targetValue   = if (selected == null) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "speakScale"
    )

    val totalWords = allWords.size

    // Текущее слово
    val currentWord = if (allWords.isNotEmpty() && currentIdx < allWords.size) allWords[currentIdx] else null

    // Генерировать варианты ответа
    fun buildOptions(word: String): List<String> {
        val pool   = GuessWordData.forLevel(levelNum).filter { it != word }
        val wrong  = pool.shuffled().take(3)
        return (wrong + word).shuffled()
    }

    // Инициализируем варианты для первого слова
    LaunchedEffect(currentIdx) {
        selected   = null
        showResult = false
        if (currentWord != null) {
            options = buildOptions(currentWord)
            // Озвучиваем слово автоматически
            delay(300)
            tts?.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // Обработка ответа
    fun onAnswer(answer: String) {
        if (selected != null || currentWord == null) return
        selected   = answer
        showResult = true
        if (answer == currentWord) {
            score++
        } else {
            lives--
        }
    }

    // Переход к следующему слову
    LaunchedEffect(showResult) {
        if (!showResult) return@LaunchedEffect
        delay(1200)
        if (lives <= 0) {
            gameOver = true
        } else if (currentIdx + 1 >= totalWords) {
            gameWon = true
        } else {
            currentIdx++
        }
    }

    // Перезапуск
    fun restart() {
        lives      = 3
        score      = 0
        currentIdx = 0
        selected   = null
        showResult = false
        gameOver   = false
        gameWon    = false
    }

    when {
        gameOver || gameWon -> GameOverScreen(
            score     = score,
            onBack    = onBack,
            onRestart = ::restart
        )
        currentWord == null -> {
            // Нет слов
            Box(Modifier.fillMaxSize().background(SoyleBg), Alignment.Center) {
                Text("Нет слов для этого уровня", color = SoyleTextSecondary)
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoyleBg)
            ) {
                // Верхняя панель
                GameTopBar(
                    title  = "Уровень $levelNum",
                    lives  = lives,
                    score  = score,
                    onBack = onBack
                )

                // Прогресс
                LinearProgressIndicator(
                    progress          = { (currentIdx + 1).toFloat() / totalWords },
                    modifier          = Modifier.fillMaxWidth().height(3.dp),
                    color             = SoyleAccent,
                    trackColor        = SoyleSurface2
                )

                Spacer(Modifier.height(32.dp))

                // Кнопка «Послушать»
                Box(
                    modifier         = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .scale(speakScale)
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(SoyleAccent.copy(alpha = 0.15f))
                            .border(2.dp, SoyleAccent.copy(alpha = 0.4f), CircleShape)
                            .clickable {
                                tts?.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, null)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector        = Icons.Outlined.VolumeUp,
                                contentDescription = "Послушать",
                                tint               = SoyleAccent,
                                modifier           = Modifier.size(36.dp)
                            )
                            Text(
                                text      = "Слушай!",
                                fontSize  = 11.sp,
                                color     = SoyleAccent,
                                fontWeight = FontWeight.Medium,
                                modifier  = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text      = "Какое слово прозвучало?",
                    fontSize  = 15.sp,
                    color     = SoyleTextSecondary,
                    modifier  = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(28.dp))

                // Варианты ответа
                Column(
                    modifier            = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    options.forEach { option ->
                        AnswerButton(
                            text        = option,
                            isCorrect   = currentWord == option,
                            isSelected  = selected == option,
                            showResult  = showResult,
                            onClick     = { onAnswer(option) }
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // Счётчик слов
                Text(
                    text     = "${currentIdx + 1} / $totalWords",
                    fontSize = 12.sp,
                    color    = SoyleTextMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ── Кнопка ответа ─────────────────────────────────────────────────────────────

@Composable
private fun AnswerButton(
    text       : String,
    isCorrect  : Boolean,
    isSelected : Boolean,
    showResult : Boolean,
    onClick    : () -> Unit
) {
    val bgColor = when {
        showResult && isCorrect  -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        showResult && isSelected -> Color(0xFFE53935).copy(alpha = 0.2f)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = !showResult, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = text,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Medium,
            color      = textColor
        )
        if (showResult) {
            val icon = if (isCorrect) Icons.Outlined.CheckCircle else if (isSelected) Icons.Outlined.Cancel else null
            icon?.let {
                Icon(
                    imageVector        = it,
                    contentDescription = null,
                    tint               = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935),
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
    }
}
