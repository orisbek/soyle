package com.example.soyle.ui.screens.game

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.ui.screens.exercise.ExerciseUiState
import com.example.soyle.ui.screens.exercise.ExerciseViewModel
import com.example.soyle.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PoemsScreen(onBack: () -> Unit = {}, viewModel: ExerciseViewModel = hiltViewModel()) {
    val context  = LocalContext.current
    val uiState  by viewModel.uiState.collectAsState()
    val poems    = remember { RhymesData.all }

    var lives        by remember { mutableIntStateOf(3) }
    var score        by remember { mutableIntStateOf(0) }
    var poemIdx      by remember { mutableIntStateOf(0) }
    var lineIdx      by remember { mutableIntStateOf(0) }
    var lastFeedback by remember { mutableStateOf("") }
    var gameOver     by remember { mutableStateOf(false) }
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }

    val totalPoems = poems.size
    val currentPoem = if (poemIdx < totalPoems) poems[poemIdx] else null
    val currentLine = currentPoem?.lines?.getOrNull(lineIdx)

    LaunchedEffect(poemIdx, lineIdx) { viewModel.reset(); lastFeedback = "" }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is ExerciseUiState.Success -> {
                lastFeedback = s.feedback
                if (s.score >= 40) score++
                else lives--
                delay(1800)
                if (lives <= 0) { gameOver = true; return@LaunchedEffect }
                // Переходим к следующей строке или стихотворению
                val poem = poems.getOrNull(poemIdx) ?: run { gameOver = true; return@LaunchedEffect }
                if (lineIdx + 1 < poem.lines.size) {
                    lineIdx++
                } else if (poemIdx + 1 < totalPoems) {
                    poemIdx++; lineIdx = 0
                } else {
                    gameOver = true
                }
            }
            is ExerciseUiState.Error -> {
                lastFeedback = "Попробуй ещё раз"
                lives--
                delay(1500)
                if (lives <= 0) gameOver = true
                else viewModel.reset()
            }
            else -> Unit
        }
    }

    fun restart() { lives = 3; score = 0; poemIdx = 0; lineIdx = 0; lastFeedback = ""; gameOver = false; viewModel.reset() }

    when {
        gameOver -> GameOverScreen(score = score, onBack = onBack, onRestart = ::restart)
        currentLine == null -> Box(Modifier.fillMaxSize().background(SoyleBg), Alignment.Center) {
            Text("Нет стихов", color = SoyleTextSecondary)
        }
        else -> {
            val totalLines = poems.sumOf { it.lines.size }
            val doneLines  = poems.take(poemIdx).sumOf { it.lines.size } + lineIdx

            Column(Modifier.fillMaxSize().background(SoyleBg)) {
                GameTopBar("Стишки с «Р»", lives, score, onBack)
                LinearProgressIndicator(
                    progress = { (doneLines + 1).toFloat() / totalLines },
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    color = SoyleAccent, trackColor = SoyleSurface2
                )

                Spacer(Modifier.weight(1f))

                // Номер стихотворения
                Text(
                    "Стихотворение ${poemIdx + 1} из $totalPoems",
                    fontSize = 12.sp, color = SoyleTextMuted,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))

                // Карточка стихотворения — показываем всё, подсвечиваем текущую строку
                Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp), Alignment.Center) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SoyleSurface)
                            .border(1.5.dp, SoyleBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        currentPoem!!.lines.forEachIndexed { idx, line ->
                            val isActive = idx == lineIdx
                            val annotated = buildAnnotatedString {
                                line.forEach { ch ->
                                    if (ch.lowercaseChar() == 'р') {
                                        withStyle(SpanStyle(
                                            color      = if (isActive) SoyleAccent else SoyleAccent.copy(alpha = 0.5f),
                                            fontWeight = FontWeight.ExtraBold
                                        )) { append(ch) }
                                    } else {
                                        withStyle(SpanStyle(
                                            color = if (isActive) SoyleTextPrimary else SoyleTextMuted
                                        )) { append(ch) }
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isActive) SoyleAccentSoft else Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    annotated,
                                    fontSize   = if (isActive) 20.sp else 17.sp,
                                    lineHeight = 26.sp,
                                    textAlign  = TextAlign.Center,
                                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier   = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    "Прочитай выделенную строку",
                    fontSize = 13.sp, color = SoyleTextSecondary,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Кнопка микрофона
                val isRec  = uiState is ExerciseUiState.Recording
                val isAnal = uiState is ExerciseUiState.Analyzing
                val pulse by rememberInfiniteTransition(label = "p").animateFloat(
                    1f, if (isRec) 1.08f else 1f,
                    infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "ps"
                )
                Box(Modifier.fillMaxWidth(), Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .scale(pulse).size(80.dp).clip(CircleShape)
                            .background(if (isRec) Color(0xFFE53935).copy(0.15f) else SoyleAccentSoft)
                            .border(2.dp, if (isRec) Color(0xFFE53935) else SoyleAccent, CircleShape)
                            .clickable(enabled = !isAnal) {
                                if (!hasPermission) { permLauncher.launch(Manifest.permission.RECORD_AUDIO); return@clickable }
                                if (isRec) viewModel.stopRecording()
                                else viewModel.startRecording(currentLine.lowercase(), ExerciseMode.WORD)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAnal) CircularProgressIndicator(color = SoyleAccent, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
                        else Icon(if (isRec) Icons.Outlined.Stop else Icons.Outlined.Mic, null,
                            tint = if (isRec) Color(0xFFE53935) else SoyleAccent, modifier = Modifier.size(32.dp))
                    }
                }

                AnimatedVisibility(visible = lastFeedback.isNotBlank(), modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
                    FeedbackBanner(
                        feedback  = lastFeedback,
                        isSuccess = uiState is ExerciseUiState.Success && (uiState as ExerciseUiState.Success).score >= 40
                    )
                }

                Spacer(Modifier.weight(1f))
            }
        }
    }
}
