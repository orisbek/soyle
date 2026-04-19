package com.example.soyle.ui.screens.exercise

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.domain.model.MascotEmotion
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoWhite)
            .padding(bottom = 24.dp)
    ) {
        // ── Top bar ────────────────────────────────────────────────────────
        DuoTopBar(
            progress = null,
            onBack   = onBack,
            trailing = {
                DuoHeartRow(lives = 3)
            }
        )

        // ── Контент ────────────────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            // Маскот с подсказкой
            AnimatedContent(targetState = uiState, label = "mascot") { state ->
                val mascotText = when (state) {
                    is ExerciseUiState.Idle      -> "Произнеси звук «$phoneme»"
                    is ExerciseUiState.Recording -> "Слушаю... говори!"
                    is ExerciseUiState.Analyzing -> "Анализирую произношение..."
                    is ExerciseUiState.Success   -> state.feedback
                    is ExerciseUiState.Error     -> "Что-то пошло не так"
                }
                DuoMascotSpeech(text = mascotText)
            }

            // ── Большая буква ──────────────────────────────────────────────
            Text(
                text       = phoneme,
                fontSize   = 120.sp,
                fontWeight = FontWeight.Black,
                color      = DuoGreen
            )

            Text(
                text = when (exerciseMode) {
                    ExerciseMode.GAME_RHYTHM -> "Игра: держите ритм произношения"
                    ExerciseMode.GAME_ECHO -> "Игра: повторите фразу без пауз"
                    ExerciseMode.GAME_PUZZLE -> "Игра: соберите слово по слогам"
                    else -> "Тренировка произношения"
                },
                fontSize = 14.sp,
                color = DuoTextSecondary
            )

            // ── Центральная зона ───────────────────────────────────────────
            AnimatedContent(targetState = uiState, label = "content") { state ->
                when (state) {
                    is ExerciseUiState.Idle -> {
                        Text(
                            text     = "Нажми кнопку и произнеси звук",
                            fontSize = 14.sp,
                            color    = DuoTextSecondary
                        )
                    }
                    is ExerciseUiState.Recording -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                                        LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color    = DuoRed,
                                trackColor = DuoRedLight
                            )
                        }
                    }
                    is ExerciseUiState.Analyzing -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = DuoGreen, modifier = Modifier.size(48.dp))
                            Text("Анализирую...", fontSize = 14.sp, color = DuoTextSecondary)
                        }
                    }
                    is ExerciseUiState.Success -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DuoScoreCircle(score = state.score)
                            DuoXpBadge(xp = state.xp)
                        }
                    }
                    is ExerciseUiState.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(state.message, color = DuoRed, fontSize = 14.sp)
                            TextButton(onClick = { viewModel.reset() }) {
                                Text("Попробовать снова", color = DuoGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ── Кнопка записи ─────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            if (!hasMicPermission) {
                DuoButton(
                    text    = "Разрешить микрофон",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                )
            } else {
                RecordButton(
                    isRecording = uiState is ExerciseUiState.Recording,
                    enabled     = uiState is ExerciseUiState.Idle ||
                            uiState is ExerciseUiState.Recording ||
                            uiState is ExerciseUiState.Error,
                    onClick     = {
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