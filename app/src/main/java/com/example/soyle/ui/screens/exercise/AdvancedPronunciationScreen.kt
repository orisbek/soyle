package com.example.soyle.ui.screens.exercise

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AdvancedPronunciationScreen(
    expectedWord: String = "Р",
    onBack: () -> Unit,
    viewModel: PronunciationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val attemptsCount by viewModel.attemptsCount.collectAsState()
    val lastScore by viewModel.lastScore.collectAsState()
    val context = LocalContext.current

    // Проверка разрешений
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Заголовок и Прогресс
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Тренировка звука",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = expectedWord,
                fontSize = 120.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF3F51B5)
            )
            
            if (attemptsCount > 0) {
                Text(
                    text = "Попыток: $attemptsCount",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
            }
        }

        // Центральная зона (Результаты или Анимация)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                }
            ) { state ->
                when (state) {
                    is PronunciationUiState.Idle -> {
                        Text(
                            text = "Нажми на кнопку и произнеси «$expectedWord»",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = Color.DarkGray
                        )
                    }
                    is PronunciationUiState.Listening -> {
                        RecordingPulseAnimation()
                    }
                    is PronunciationUiState.Result -> {
                        ResultView(state)
                    }
                    is PronunciationUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Кнопка записи
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!hasMicPermission) {
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Разрешить микрофон 🎙")
                }
            } else {
                RecordButton(
                    isRecording = uiState is PronunciationUiState.Listening,
                    onClick = {
                        if (uiState is PronunciationUiState.Result || uiState is PronunciationUiState.Error) {
                            viewModel.reset()
                        } else {
                            viewModel.startListening(expectedWord)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ResultView(state: PronunciationUiState.Result) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        val scoreAnim by animateIntAsState(
            targetValue = state.analysis.score,
            animationSpec = tween(durationMillis = 1000)
        )

        Text(
            text = if (state.analysis.score >= 80) "Отлично!" else "Попробуй ещё!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(state.color)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = scoreAnim / 100f,
                modifier = Modifier.size(150.dp),
                strokeWidth = 12.dp,
                color = Color(state.color),
                trackColor = Color(state.color).copy(alpha = 0.2f)
            )
            Text(
                text = "$scoreAnim%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = state.analysis.feedback,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun RecordingPulseAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .scale(scale)
            .background(Color(0xFFF44336).copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun RecordButton(isRecording: Boolean, onClick: () -> Unit) {
    val color = if (isRecording) Color.Red else Color(0xFF4CAF50)
    
    LargeFloatingActionButton(
        onClick = onClick,
        containerColor = color,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.size(80.dp)
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Default.Refresh else Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
    }
}
