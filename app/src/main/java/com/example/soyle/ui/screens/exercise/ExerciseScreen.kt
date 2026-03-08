package com.example.soyle.ui.screens.exercise

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.soyle.ui.components.MascotView
import com.example.soyle.ui.components.RecordButton
import com.example.soyle.ui.components.ScoreBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    phoneme   : String,
    mode      : String,
    onResult  : (score: Int) -> Unit,
    onBack    : () -> Unit,
    viewModel : ExerciseViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsState()
    val exerciseMode = remember { ExerciseMode.valueOf(mode) }
    val context      = LocalContext.current

    // ── Разрешение на микрофон ─────────────────────────────────────────────
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PermissionChecker.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
    }

    // ── Переход на результат ───────────────────────────────────────────────
    LaunchedEffect(uiState) {
        if (uiState is ExerciseUiState.Success) {
            kotlinx.coroutines.delay(2000)
            onResult((uiState as ExerciseUiState.Success).score)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Звук «$phoneme»") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            // ── Большая буква ──────────────────────────────────────────────
            Text(
                text       = phoneme,
                fontSize   = 120.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.primary
            )

            // ── Центральная зона ───────────────────────────────────────────
            AnimatedContent(
                targetState = uiState,
                label       = "exerciseContent"
            ) { state ->
                when (state) {
                    is ExerciseUiState.Idle -> {
                        MascotView(emotion = MascotEmotion.GREETING)
                    }
                    is ExerciseUiState.Recording -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text       = "🎙 Говори...",
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    is ExerciseUiState.Analyzing -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(modifier = Modifier.size(56.dp))
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text  = "Анализирую...",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    is ExerciseUiState.Success -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ScoreBar(score = state.score)
                            MascotView(emotion = state.emotion)
                            Text(text = state.feedback, fontSize = 16.sp)
                            Text(
                                text       = "+${state.xp} XP",
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    is ExerciseUiState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = "⚠️ ${state.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(12.dp))
                            TextButton(onClick = { viewModel.reset() }) {
                                Text("Попробовать снова")
                            }
                        }
                    }
                }
            }

            // ── Кнопка записи или запрос разрешения ───────────────────────
            if (hasMicPermission) {
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
            } else {
                // Нет разрешения — показываем кнопку запроса
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = "Нужен доступ к микрофону",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    ) {
                        Text("Разрешить микрофон 🎙")
                    }
                }
            }
        }
    }
}