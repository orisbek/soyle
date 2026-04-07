package com.example.soyle.ui.screens.exercise

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

@Composable
fun ExerciseScreen(
    phoneme   : String,
    mode      : String,
    onResult  : (score: Int) -> Unit,
    onBack    : () -> Unit,
    viewModel : ExerciseViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsState()
    val exerciseMode  = remember { ExerciseMode.valueOf(mode) }
    val context       = LocalContext.current

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PermissionChecker.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasMicPermission = it }

    LaunchedEffect(uiState) {
        if (uiState is ExerciseUiState.Success) {
            kotlinx.coroutines.delay(2000)
            onResult((uiState as ExerciseUiState.Success).score)
        }
    }

    // Анимация попугая
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val mascotY by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -8f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotBounce"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidsBg)
    ) {
        // ── Красочная шапка ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(KidsMint, KidsBlue)
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
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text       = modeTitle(exerciseMode, phoneme),
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
                Spacer(Modifier.weight(1f))
                // Жизни
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { Text("❤️", fontSize = 18.sp) }
                }
            }
        }

        // ── Контент ────────────────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // ── Маскот с облачком ─────────────────────────────────────────
            AnimatedContent(targetState = uiState, label = "mascot") { state ->
                val mascotText = when (state) {
                    is ExerciseUiState.Idle      -> "Скажи звук «$phoneme»! 🎙"
                    is ExerciseUiState.Recording -> "Слушаю... говори!"
                    is ExerciseUiState.Analyzing -> "Анализирую произношение..."
                    is ExerciseUiState.Success   -> state.feedback
                    is ExerciseUiState.Error     -> "Что-то пошло не так 😅"
                }
                KidsExerciseMascot(text = mascotText, offsetY = mascotY)
            }

            // ── Большая буква с красивым фоном ────────────────────────────
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(KidsMintLight, Color(0xFFE8F9F8))
                        )
                    )
                    .border(4.dp, KidsMint.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = phoneme,
                    fontSize   = 100.sp,
                    fontWeight = FontWeight.Black,
                    color      = KidsMint
                )
            }

            // ── Статус записи ──────────────────────────────────────────────
            AnimatedContent(targetState = uiState, label = "content") { state ->
                when (state) {
                    is ExerciseUiState.Idle -> {
                        Text(
                            text      = "Нажми кнопку и произнеси звук",
                            fontSize  = 15.sp,
                            color     = KidsTextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                    is ExerciseUiState.Recording -> {
                        RecordingIndicator()
                    }
                    is ExerciseUiState.Analyzing -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                color    = KidsMint,
                                modifier = Modifier.size(44.dp),
                                strokeWidth = 4.dp
                            )
                            Text(
                                "Анализирую...",
                                fontSize  = 15.sp,
                                color     = KidsTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    is ExerciseUiState.Success -> {
                        KidsSuccessResult(score = state.score, xp = state.xp)
                    }
                    is ExerciseUiState.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("😅 ${state.message}", color = KidsPink, fontSize = 14.sp, textAlign = TextAlign.Center)
                            TextButton(onClick = { viewModel.reset() }) {
                                Text("Попробовать снова", color = KidsMint, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }

        // ── Кнопка записи ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!hasMicPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.horizontalGradient(listOf(KidsMint, KidsBlue)))
                        .clickable { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "🎙 РАЗРЕШИТЬ МИКРОФОН",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                }
            } else {
                KidsRecordButton(
                    isRecording = uiState is ExerciseUiState.Recording,
                    enabled     = uiState is ExerciseUiState.Idle ||
                            uiState is ExerciseUiState.Recording ||
                            uiState is ExerciseUiState.Error,
                    onClick = {
                        when (uiState) {
                            is ExerciseUiState.Recording -> viewModel.stopRecording()
                            else -> viewModel.startRecording(phoneme, exerciseMode)
                        }
                    }
                )
            }
        }
    }
}

// ── Маскот ────────────────────────────────────────────────────────────────────

@Composable
private fun KidsExerciseMascot(text: String, offsetY: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(KidsMintLight, Color(0xFFE8F0FF)),
                    start  = Offset.Zero,
                    end    = Offset(Float.POSITIVE_INFINITY, 0f)
                )
            )
            .border(3.dp, KidsMint.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text     = "🦜",
            fontSize = 44.sp,
            modifier = Modifier.offset(y = offsetY.dp)
        )
        Text(
            text       = text,
            fontSize   = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color      = KidsTextPrimary,
            lineHeight = 22.sp,
            modifier   = Modifier.weight(1f)
        )
    }
}

// ── Индикатор записи ──────────────────────────────────────────────────────────

@Composable
private fun RecordingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "rec")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 0.8f,
        targetValue   = 1.2f,
        animationSpec = infiniteRepeatable(
            animation  = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🎙", fontSize = 44.sp, modifier = Modifier.scale(scale))
        // Волна записи
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            repeat(7) { i ->
                val barScale by infiniteTransition.animateFloat(
                    initialValue  = 0.3f,
                    targetValue   = 1f,
                    animationSpec = infiniteRepeatable(
                        animation   = tween(300 + i * 60),
                        repeatMode  = RepeatMode.Reverse
                    ),
                    label = "bar$i"
                )
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height((8 + 24 * barScale).dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(KidsPink)
                )
            }
        }
    }
}

// ── Результат успеха ──────────────────────────────────────────────────────────

@Composable
private fun KidsSuccessResult(score: Int, xp: Int) {
    val color = scoreColor(score)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text     = if (score >= 80) "🎉" else if (score >= 60) "👍" else "💪",
            fontSize = 44.sp
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(4.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "$score%",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Black,
                color      = color
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(KidsYellowLight)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("⭐", fontSize = 18.sp)
            Text(
                text       = "+$xp XP",
                fontSize   = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = KidsYellowDark
            )
        }
    }
}

// ── Большая кнопка записи в детском стиле ────────────────────────────────────

@Composable
private fun KidsRecordButton(
    isRecording: Boolean,
    enabled    : Boolean,
    onClick    : () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = if (isRecording) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btnPulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isRecording)
                        Brush.radialGradient(listOf(KidsPink, Color(0xFFCC0044)))
                    else if (enabled)
                        Brush.radialGradient(listOf(KidsMint, KidsMintDark))
                    else
                        Brush.radialGradient(listOf(Color(0xFFCCCCCC), Color(0xFFAAAAAA)))
                )
                .border(4.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = if (isRecording) "⏹" else "🎙",
                fontSize = 36.sp
            )
        }
        Text(
            text       = if (isRecording) "СТОП" else "ГОВОРИ",
            fontSize   = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = if (isRecording) KidsPink else KidsMint
        )
    }
}

private fun modeTitle(mode: ExerciseMode, phoneme: String) = when (mode) {
    ExerciseMode.SOUND         -> "Звук «$phoneme»"
    ExerciseMode.SYLLABLE      -> "Слоги с «$phoneme»"
    ExerciseMode.WORD          -> "Слова с «$phoneme»"
    ExerciseMode.LISTEN_CHOOSE -> "Послушай и выбери"
    ExerciseMode.VISUALIZE     -> "Волна звука"
    ExerciseMode.GAME          -> "Игра"
}
