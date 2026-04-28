package com.example.soyle.ui.screens.exercise

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Слова для практики по фонеме ──────────────────────────────────────────────

private fun practiceWordsFor(phoneme: String): List<String> = when (phoneme.uppercase()) {
    "Р"  -> listOf("рыба", "рак", "рот", "трава", "дорога")
    "Л"  -> listOf("лодка", "лиса", "лук", "молоко", "пол")
    "Ш"  -> listOf("шар", "шапка", "мышь", "кошка", "машина")
    "С"  -> listOf("сад", "сок", "слон", "сова", "собака")
    "З"  -> listOf("заяц", "зима", "земля", "зонт", "звезда")
    "Ч"  -> listOf("чай", "час", "ночь", "дочка", "мяч")
    "Щ"  -> listOf("щука", "щит", "роща", "вещи", "ящик")
    "Ж"  -> listOf("жук", "жар", "ежи", "нож", "кожа")
    else -> listOf(phoneme.lowercase(), phoneme.lowercase().repeat(2))
}

// ── Экран упражнения ──────────────────────────────────────────────────────────

@Composable
fun ExerciseScreen(
    phoneme   : String = "Р",
    title     : String = "Звук «Р»",
    onBack    : () -> Unit = {},
    onResult  : (Int) -> Unit = {},
    viewModel : ExerciseViewModel = hiltViewModel()
) {
    val context  = LocalContext.current
    val uiState  by viewModel.uiState.collectAsState()

    val words       = remember(phoneme) { practiceWordsFor(phoneme) }
    var currentWord by remember { mutableIntStateOf(0) }
    val targetWord  = words[currentWord]

    // Когда слово выбрано — используем WORD mode, для одного звука — SOUND
    val exerciseMode = if (words[currentWord].length == 1) ExerciseMode.SOUND else ExerciseMode.WORD

    // ── Runtime-разрешение на микрофон ────────────────────────────────────
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionDenied = !granted
        if (granted) viewModel.startRecording(targetWord, exerciseMode)
    }

    // ── После получения результата — передаём score наверх ────────────────
    LaunchedEffect(uiState) {
        if (uiState is ExerciseUiState.Success) {
            // Небольшая задержка чтобы пользователь увидел результат
            kotlinx.coroutines.delay(2500)
            onResult((uiState as ExerciseUiState.Success).score)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SoyleBg)) {

        // ── Шапка ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoyleSurface)
                    .clickable {
                        viewModel.stopRecording()
                        viewModel.reset()
                        onBack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Назад",
                    tint               = SoyleTextSecondary,
                    modifier           = Modifier.size(16.dp)
                )
            }

            Text(
                text       = title,
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = SoyleTextPrimary
            )

            // Индикатор слова (1/5)
            Text(
                text     = "${currentWord + 1}/${words.size}",
                fontSize = 13.sp,
                color    = SoyleTextMuted
            )
        }

        // ── Центральный контент ────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            AnimatedContent(
                targetState    = uiState,
                label          = "exerciseAnim",
                transitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                }
            ) { st ->
                when (st) {
                    is ExerciseUiState.Idle -> {
                        IdleContent(
                            phoneme      = phoneme,
                            targetWord   = targetWord,
                            words        = words,
                            currentIdx   = currentWord,
                            onWordSelect = { currentWord = it }
                        )
                    }
                    is ExerciseUiState.Recording -> {
                        RecordingContent(targetWord = targetWord)
                    }
                    is ExerciseUiState.Analyzing -> {
                        AnalyzingContent()
                    }
                    is ExerciseUiState.Success -> {
                        SuccessContent(state = st)
                    }
                    is ExerciseUiState.Error -> {
                        ErrorContent(message = st.message)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Кнопка микрофона ──────────────────────────────────────────
            MicButton(
                uiState = uiState,
                onTap = {
                    when (uiState) {
                        is ExerciseUiState.Idle, is ExerciseUiState.Error -> {
                            if (hasPermission) {
                                viewModel.startRecording(targetWord, exerciseMode)
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                        is ExerciseUiState.Recording -> {
                            // Ничего — запись идёт автоматически 3 сек
                        }
                        is ExerciseUiState.Success -> {
                            currentWord = (currentWord + 1) % words.size
                            viewModel.reset()
                        }
                        else -> {}
                    }
                }
            )

            if (permissionDenied) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text      = "Разреши доступ к микрофону в настройках телефона",
                    fontSize  = 12.sp,
                    color     = SoyleRed,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Idle: фонема + выбор слова ────────────────────────────────────────────────

@Composable
private fun IdleContent(
    phoneme      : String,
    targetWord   : String,
    words        : List<String>,
    currentIdx   : Int,
    onWordSelect : (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text      = "Произнеси слово",
            fontSize  = 15.sp,
            color     = SoyleTextSecondary,
            textAlign = TextAlign.Center
        )

        // Большая буква
        Text(
            text          = phoneme,
            fontSize      = 96.sp,
            fontWeight    = FontWeight.Bold,
            color         = SoyleTextPrimary,
            letterSpacing = (-4).sp
        )

        // Выбранное слово
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(SoyleAccentSoft)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text       = targetWord.uppercase(),
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = SoyleAccent
            )
        }

        // Другие слова для выбора
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())
        ) {
            words.forEachIndexed { idx, word ->
                if (idx != currentIdx) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SoyleSurface)
                            .border(1.dp, SoyleBorder, RoundedCornerShape(20.dp))
                            .clickable { onWordSelect(idx) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(word, fontSize = 13.sp, color = SoyleTextSecondary)
                    }
                }
            }
        }
    }
}

// ── Recording: анимация волны ─────────────────────────────────────────────────

@Composable
private fun RecordingContent(targetWord: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text      = "Говори сейчас...",
            fontSize  = 20.sp,
            color     = SoyleTextPrimary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Text(
            text       = targetWord.uppercase(),
            fontSize   = 32.sp,
            fontWeight = FontWeight.Bold,
            color      = SoyleAccent
        )
        // Волна
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            repeat(9) { i ->
                val height by infiniteTransition.animateFloat(
                    initialValue  = 8f,
                    targetValue   = 38f,
                    animationSpec = infiniteRepeatable(
                        animation  = tween(280 + i * 60, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bar$i"
                )
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(height.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SoyleRed.copy(alpha = 0.6f + (i % 3) * 0.13f))
                )
            }
        }
        Text(
            text     = "Запись идёт 3 секунды",
            fontSize = 12.sp,
            color    = SoyleTextMuted
        )
    }
}

// ── Analyzing ────────────────────────────────────────────────────────────────

@Composable
private fun AnalyzingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CircularProgressIndicator(
            color       = SoyleAccent,
            modifier    = Modifier.size(52.dp),
            strokeWidth = 3.dp
        )
        Text(
            text      = "Анализирую произношение...",
            fontSize  = 16.sp,
            color     = SoyleTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ── Success ───────────────────────────────────────────────────────────────────

@Composable
private fun SuccessContent(state: ExerciseUiState.Success) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularScore(score = state.score, size = 120.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SoyleSurface)
                .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text       = state.feedback,
                fontSize   = 15.sp,
                color      = SoyleTextSecondary,
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector        = Icons.Outlined.Stars,
                contentDescription = null,
                tint               = SoyleAmber,
                modifier           = Modifier.size(18.dp)
            )
            Text(
                text       = "+${state.xp} XP",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = SoyleAmber
            )
        }

        Text(
            text     = "Следующее слово через 2 сек...",
            fontSize = 12.sp,
            color    = SoyleTextMuted
        )
    }
}

// ── Error ─────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorContent(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector        = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint               = SoyleRed,
            modifier           = Modifier.size(48.dp)
        )
        Text(
            text       = message,
            fontSize   = 15.sp,
            color      = SoyleTextSecondary,
            textAlign  = TextAlign.Center,
            lineHeight = 22.sp
        )
        Text(
            text      = "Убедись что сервер запущен:\npython3 backend_server.py",
            fontSize  = 12.sp,
            color     = SoyleTextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
        )
    }
}

// ── Кнопка микрофона ─────────────────────────────────────────────────────────

@Composable
private fun MicButton(uiState: ExerciseUiState, onTap: () -> Unit) {
    val isRecording  = uiState is ExerciseUiState.Recording
    val isAnalyzing  = uiState is ExerciseUiState.Analyzing
    val isSuccess    = uiState is ExerciseUiState.Success
    val isDisabled   = isRecording || isAnalyzing

    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = if (isRecording) 1.12f else 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(500),
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
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isRecording -> SoyleRed.copy(alpha = 0.15f)
                        isSuccess   -> SoyleAccentSoft
                        isAnalyzing -> SoyleSurface2
                        else        -> SoyleButtonPrimary.copy(alpha = 0.12f)
                    }
                )
                .border(
                    2.dp,
                    when {
                        isRecording -> SoyleRed
                        isSuccess   -> SoyleAccent
                        isAnalyzing -> SoyleBorder
                        else        -> SoyleButtonPrimary.copy(alpha = 0.4f)
                    },
                    CircleShape
                )
                .clickable(enabled = !isDisabled, onClick = onTap),
            contentAlignment = Alignment.Center
        ) {
            when {
                isRecording -> Icon(
                    imageVector        = Icons.Outlined.Stop,
                    contentDescription = "Запись",
                    tint               = SoyleRed,
                    modifier           = Modifier.size(30.dp)
                )
                isAnalyzing -> CircularProgressIndicator(
                    color       = SoyleAccent,
                    modifier    = Modifier.size(26.dp),
                    strokeWidth = 2.5.dp
                )
                isSuccess -> Icon(
                    imageVector        = Icons.Outlined.NavigateNext,
                    contentDescription = "Далее",
                    tint               = SoyleAccent,
                    modifier           = Modifier.size(32.dp)
                )
                else -> Icon(
                    imageVector        = Icons.Outlined.Mic,
                    contentDescription = "Записать",
                    tint               = SoyleTextPrimary,
                    modifier           = Modifier.size(32.dp)
                )
            }
        }

        Text(
            text = when (uiState) {
                is ExerciseUiState.Recording -> "Говори..."
                is ExerciseUiState.Analyzing -> "Анализирую..."
                is ExerciseUiState.Success   -> "Следующее слово →"
                is ExerciseUiState.Error     -> "Попробовать снова"
                else                         -> "Нажми и произнеси"
            },
            fontSize = 13.sp,
            color    = SoyleTextSecondary
        )
    }
}
