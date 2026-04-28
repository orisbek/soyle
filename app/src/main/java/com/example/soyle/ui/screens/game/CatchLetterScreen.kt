package com.example.soyle.ui.screens.game

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import com.example.soyle.ui.screens.exercise.ExerciseUiState
import com.example.soyle.ui.screens.exercise.ExerciseViewModel
import com.example.soyle.ui.theme.*

// ── Режим игры ────────────────────────────────────────────────────────────────

enum class CatchMode { SELECT, TAP, VOICE }

// ── Главный экран ─────────────────────────────────────────────────────────────

@Composable
fun CatchLetterScreen(
    onBack      : () -> Unit = {},
    viewModel   : ExerciseViewModel = hiltViewModel()
) {
    var mode by remember { mutableStateOf(CatchMode.SELECT) }

    AnimatedContent(
        targetState    = mode,
        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
        label          = "catchMode"
    ) { currentMode ->
        when (currentMode) {
            CatchMode.SELECT -> ModeSelectScreen(
                onBack    = onBack,
                onTapMode   = { mode = CatchMode.TAP },
                onVoiceMode = { mode = CatchMode.VOICE }
            )
            CatchMode.TAP   -> TapGameScreen(
                onBack = { mode = CatchMode.SELECT }
            )
            CatchMode.VOICE -> VoiceGameScreen(
                onBack    = { mode = CatchMode.SELECT },
                viewModel = viewModel
            )
        }
    }
}

// ── Экран выбора режима ────────────────────────────────────────────────────────

@Composable
private fun ModeSelectScreen(
    onBack      : () -> Unit,
    onTapMode   : () -> Unit,
    onVoiceMode : () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(SoyleBg).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Шапка
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(SoyleSurface).clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ArrowBackIosNew, null, tint = SoyleTextSecondary,
                    modifier = Modifier.size(16.dp))
            }
            Text("Поймай букву «Р»", fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = SoyleTextPrimary)
            Spacer(Modifier.size(40.dp))
        }

        Spacer(Modifier.height(48.dp))

        // Большая буква
        Box(
            modifier = Modifier.size(120.dp).clip(CircleShape).background(SoyleAccentSoft),
            contentAlignment = Alignment.Center
        ) {
            Text("Р", fontSize = 72.sp, fontWeight = FontWeight.Bold, color = SoyleAccent)
        }

        Spacer(Modifier.height(16.dp))
        Text("Выбери режим игры", fontSize = 16.sp, color = SoyleTextSecondary)
        Spacer(Modifier.height(40.dp))

        // Вариант 1 — Нажатие
        ModeCard(
            icon        = Icons.Outlined.TouchApp,
            title       = "Нажатие",
            description = "Нажимай только на букву «Р»!\nЗа ошибку теряешь жизнь.",
            color       = SoyleAccent,
            onClick     = onTapMode
        )

        Spacer(Modifier.height(16.dp))

        // Вариант 2 — Голос
        ModeCard(
            icon        = Icons.Outlined.Mic,
            title       = "Голос",
            description = "На экране появляются буквы —\nпроизноси каждую вслух!",
            color       = Color(0xFF22C55E),
            onClick     = onVoiceMode
        )
    }
}

@Composable
private fun ModeCard(
    icon        : androidx.compose.ui.graphics.vector.ImageVector,
    title       : String,
    description : String,
    color       : Color,
    onClick     : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = SoyleTextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(description, fontSize = 13.sp, color = SoyleTextSecondary, lineHeight = 19.sp)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = SoyleTextMuted,
            modifier = Modifier.size(20.dp))
    }
}

// ── Игра 1: Нажатие ───────────────────────────────────────────────────────────

@Composable
private fun TapGameScreen(onBack: () -> Unit) {
    var lives    by remember { mutableIntStateOf(3) }
    var score    by remember { mutableIntStateOf(0) }
    var grid     by remember { mutableStateOf(CatchLetterData.generateGrid()) }
    var tapped   by remember { mutableStateOf(setOf<Int>()) }
    var gameOver by remember { mutableStateOf(false) }
    var flashRed by remember { mutableStateOf(false) }

    // Когда все Р в текущей сетке нажаты → новая сетка
    val rIndices = remember(grid) { grid.indices.filter { grid[it] == "Р" }.toSet() }
    LaunchedEffect(tapped, rIndices) {
        if (rIndices.isNotEmpty() && tapped.containsAll(rIndices)) {
            kotlinx.coroutines.delay(400)
            grid   = CatchLetterData.generateGrid()
            tapped = emptySet()
        }
    }

    if (gameOver) {
        GameOverScreen(score = score, onBack = onBack,
            onRestart = {
                lives = 3; score = 0
                grid = CatchLetterData.generateGrid()
                tapped = emptySet()
                gameOver = false
            })
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().background(SoyleBg).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameTopBar(title = "Нажимай «Р»!", lives = lives, score = score, onBack = onBack)
        Spacer(Modifier.height(8.dp))
        Text("Нажимай только на букву «Р»",
            fontSize = 14.sp, color = SoyleTextSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))

        // Сетка букв
        LazyVerticalGrid(
            columns             = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement   = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(grid) { index, letter ->
                val isR       = letter == "Р"
                val isTapped  = index in tapped
                LetterCell(
                    letter   = letter,
                    isTapped = isTapped,
                    isR      = isR,
                    onClick  = {
                        if (!isTapped) {
                            if (isR) {
                                tapped = tapped + index
                                score++
                            } else {
                                // Ошибка!
                                lives--
                                flashRed = true
                                if (lives <= 0) gameOver = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LetterCell(
    letter   : String,
    isTapped : Boolean,
    isR      : Boolean,
    onClick  : () -> Unit
) {
    val bg = when {
        isTapped && isR -> Color(0xFF22C55E).copy(alpha = 0.2f)
        else            -> SoyleSurface
    }
    val border = when {
        isTapped && isR -> Color(0xFF22C55E)
        else            -> SoyleBorder
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(12.dp))
            .clickable(enabled = !isTapped, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = if (isTapped) "✓" else letter,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = when {
                isTapped && isR -> Color(0xFF22C55E)
                letter == "Р"   -> SoyleAccent
                else            -> SoyleTextPrimary
            }
        )
    }
}

// ── Игра 2: Голос ─────────────────────────────────────────────────────────────

@Composable
private fun VoiceGameScreen(
    onBack    : () -> Unit,
    viewModel : ExerciseViewModel
) {
    val context   = LocalContext.current
    val uiState   by viewModel.uiState.collectAsState()
    val sequence  = remember { CatchLetterData.generateSequence() }

    var currentIdx by remember { mutableIntStateOf(0) }
    var lives      by remember { mutableIntStateOf(3) }
    var score      by remember { mutableIntStateOf(0) }
    var gameOver   by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<Boolean?>(null) } // true=ok, false=wrong

    val currentLetter = if (currentIdx < sequence.size) sequence[currentIdx] else "Р"

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) viewModel.startRecording(currentLetter, ExerciseMode.SOUND)
    }

    var motivationText by remember { mutableStateOf("") }

    // Обрабатываем результат
    LaunchedEffect(uiState) {
        if (uiState is ExerciseUiState.Success) {
            val ok = (uiState as ExerciseUiState.Success).score >= 60
            val fb = (uiState as ExerciseUiState.Success).feedback
            lastResult = ok
            motivationText = if (fb.isNotBlank()) fb else if (ok) "Отлично! Буква «$currentLetter» звучит хорошо!" else "Попробуй ещё! Звук «$currentLetter»"
            kotlinx.coroutines.delay(1200)
            if (ok) {
                score++
            } else {
                lives--
                if (lives <= 0) { gameOver = true; return@LaunchedEffect }
            }
            currentIdx++
            if (currentIdx >= sequence.size) { gameOver = true; return@LaunchedEffect }
            lastResult = null
            motivationText = ""
            viewModel.reset()
        }
        if (uiState is ExerciseUiState.Error) {
            motivationText = "Не расслышал, попробуй ещё раз"
            kotlinx.coroutines.delay(900)
            lives--
            motivationText = ""
            if (lives <= 0) gameOver = true
            else { currentIdx++; viewModel.reset() }
        }
    }

    if (gameOver) {
        GameOverScreen(score = score, onBack = onBack,
            onRestart = {
                lives = 3; score = 0; currentIdx = 0
                gameOver = false; lastResult = null
                viewModel.reset()
            })
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().background(SoyleBg).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameTopBar(
            title  = "Произноси букву!",
            lives  = lives,
            score  = score,
            onBack = { viewModel.reset(); onBack() }
        )
        Spacer(Modifier.height(8.dp))
        // Прогресс
        LinearProgressIndicator(
            progress          = { currentIdx.toFloat() / sequence.size },
            modifier          = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color             = SoyleAccent,
            trackColor        = SoyleSurface2
        )
        Spacer(Modifier.height(48.dp))

        // Большая буква
        val bgColor = when (lastResult) {
            true  -> Color(0xFF22C55E).copy(alpha = 0.15f)
            false -> SoyleRed.copy(alpha = 0.15f)
            else  -> SoyleAccentSoft
        }
        val borderColor = when (lastResult) {
            true  -> Color(0xFF22C55E)
            false -> SoyleRed
            else  -> SoyleAccent
        }
        Box(
            modifier = Modifier.size(160.dp).clip(RoundedCornerShape(28.dp))
                .background(bgColor)
                .border(2.dp, borderColor, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (lastResult == true) {
                Icon(Icons.Outlined.Check, null, tint = Color(0xFF22C55E),
                    modifier = Modifier.size(72.dp))
            } else if (lastResult == false) {
                Icon(Icons.Outlined.Close, null, tint = SoyleRed,
                    modifier = Modifier.size(72.dp))
            } else {
                Text(currentLetter, fontSize = 96.sp, fontWeight = FontWeight.Bold,
                    color = SoyleAccent)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = when (uiState) {
                is ExerciseUiState.Recording -> "Говори..."
                is ExerciseUiState.Analyzing -> "Анализирую..."
                else -> "Произнеси эту букву"
            },
            fontSize = 16.sp, color = SoyleTextSecondary
        )

        Spacer(Modifier.weight(1f))

        // Кнопка микрофона
        val isRecording = uiState is ExerciseUiState.Recording
        val isAnalyzing = uiState is ExerciseUiState.Analyzing
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulse by infiniteTransition.animateFloat(
            initialValue  = 1f, targetValue  = if (isRecording) 1.1f else 1f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
            label = "p"
        )
        Box(
            modifier = Modifier.scale(pulse).size(80.dp).clip(CircleShape)
                .background(if (isRecording) SoyleRed.copy(0.15f) else SoyleAccentSoft)
                .border(2.dp, if (isRecording) SoyleRed else SoyleAccent, CircleShape)
                .clickable(enabled = !isRecording && !isAnalyzing && lastResult == null) {
                    if (hasPermission) viewModel.startRecording(currentLetter, ExerciseMode.SOUND)
                    else permLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
            contentAlignment = Alignment.Center
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(color = SoyleAccent,
                    modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
            } else {
                Icon(
                    if (isRecording) Icons.Outlined.Stop else Icons.Outlined.Mic,
                    null, tint = if (isRecording) SoyleRed else SoyleAccent,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
        // Мотивационный текст от бэкенда
        AnimatedVisibility(
            visible = motivationText.isNotBlank(),
            enter   = fadeIn(tween(200)) + expandVertically(),
            exit    = fadeOut(tween(200)) + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (lastResult == true) Color(0xFF4CAF50).copy(alpha = 0.12f)
                        else SoyleSurface2
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (lastResult == true) Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                    contentDescription = null,
                    tint = if (lastResult == true) Color(0xFF4CAF50) else SoyleTextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text     = motivationText,
                    fontSize = 13.sp,
                    color    = SoyleTextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Общие компоненты игр ──────────────────────────────────────────────────────

@Composable
fun GameTopBar(title: String, lives: Int, score: Int, onBack: () -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape)
                .background(SoyleSurface).clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.ArrowBackIosNew, null, tint = SoyleTextSecondary,
                modifier = Modifier.size(16.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                color = SoyleTextPrimary)
            // Жизни
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { i ->
                    Icon(
                        if (i < lives) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        null,
                        tint     = if (i < lives) SoyleRed else SoyleTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Box(
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                .background(SoyleAccentSoft).padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("$score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SoyleAccent)
        }
    }
}

@Composable
fun GameOverScreen(score: Int, onBack: () -> Unit, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(SoyleBg).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.EmojiEvents, null, tint = SoyleAmber,
            modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(16.dp))
        Text("Игра окончена!", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = SoyleTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Твой счёт: $score", fontSize = 20.sp, color = SoyleAccent,
            fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(40.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(52.dp)
                .clip(RoundedCornerShape(14.dp)).background(SoyleButtonPrimary)
                .clickable(onClick = onRestart),
            contentAlignment = Alignment.Center
        ) {
            Text("Играть ещё раз", fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                color = SoyleButtonPrimaryText)
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SoyleSurface).border(1.dp, SoyleBorder, RoundedCornerShape(14.dp))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text("Выйти", fontSize = 15.sp, color = SoyleTextSecondary)
        }
    }
}
