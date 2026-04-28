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
import androidx.compose.ui.text.font.FontStyle
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

private enum class TwisterLvl { SELECT, LEVEL1, LEVEL2, LEVEL3 }

@Composable
fun TongueTwistersScreen(onBack: () -> Unit = {}) {
    var level by remember { mutableStateOf(TwisterLvl.SELECT) }

    AnimatedContent(
        targetState    = level,
        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
        label          = "twisterLevel"
    ) { current ->
        when (current) {
            TwisterLvl.SELECT -> TwisterLevelSelect(onBack = onBack, onSelect = { level = it })
            TwisterLvl.LEVEL1 -> TwisterGame(levelNum = 1, onBack = { level = TwisterLvl.SELECT })
            TwisterLvl.LEVEL2 -> TwisterGame(levelNum = 2, onBack = { level = TwisterLvl.SELECT })
            TwisterLvl.LEVEL3 -> TwisterGame(levelNum = 3, onBack = { level = TwisterLvl.SELECT })
        }
    }
}

@Composable
private fun TwisterLevelSelect(onBack: () -> Unit, onSelect: (TwisterLvl) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, "Назад", tint = SoyleTextPrimary)
        }
        Spacer(Modifier.height(16.dp))
        Text("Скороговорки", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = SoyleTextPrimary)
        Text(
            "Говори чётко — побей свой рекорд!",
            fontSize = 14.sp, color = SoyleTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )
        listOf(
            Triple(TwisterLvl.LEVEL1, "Лёгкий",   Color(0xFF4CAF50)),
            Triple(TwisterLvl.LEVEL2, "Средний",  SoyleAccent),
            Triple(TwisterLvl.LEVEL3, "Сложный",  Color(0xFFE53935)),
        ).forEachIndexed { i, (lvl, title, color) ->
            if (i > 0) Spacer(Modifier.height(14.dp))
            WordsLevelCard(number = i + 1, title = title,
                description = TongueTwistersData.forLevel(i + 1).firstOrNull()?.text?.take(40)?.plus("…") ?: "",
                color = color, onClick = { onSelect(lvl) })
        }
    }
}

@Composable
private fun TwisterGame(levelNum: Int, onBack: () -> Unit, viewModel: ExerciseViewModel = hiltViewModel()) {
    val context   = LocalContext.current
    val uiState   by viewModel.uiState.collectAsState()
    val twisters  = remember(levelNum) { TongueTwistersData.forLevel(levelNum) }

    var lives        by remember { mutableIntStateOf(3) }
    var score        by remember { mutableIntStateOf(0) }
    var currentIdx   by remember { mutableIntStateOf(0) }
    var lastFeedback by remember { mutableStateOf("") }
    var gameOver     by remember { mutableStateOf(false) }
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }

    val total   = twisters.size
    val current = if (currentIdx < total) twisters[currentIdx] else null

    LaunchedEffect(currentIdx) { viewModel.reset(); lastFeedback = "" }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is ExerciseUiState.Success -> {
                lastFeedback = s.feedback
                if (s.score >= 50) score++ else lives--
                delay(2000)
                if (lives <= 0 || currentIdx + 1 >= total) gameOver = true
                else { currentIdx++; viewModel.reset() }
            }
            is ExerciseUiState.Error -> {
                lastFeedback = s.message
                lives--
                delay(1500)
                if (lives <= 0 || currentIdx + 1 >= total) gameOver = true
                else { currentIdx++; viewModel.reset() }
            }
            else -> Unit
        }
    }

    fun restart() { lives = 3; score = 0; currentIdx = 0; lastFeedback = ""; gameOver = false; viewModel.reset() }

    when {
        gameOver    -> GameOverScreen(score = score, onBack = onBack, onRestart = ::restart)
        current == null -> Box(Modifier.fillMaxSize().background(SoyleBg), Alignment.Center) { Text("Нет скороговорок", color = SoyleTextSecondary) }
        else -> Column(Modifier.fillMaxSize().background(SoyleBg)) {
            GameTopBar("Скороговорки", lives, score, onBack)
            LinearProgressIndicator(
                progress = { (currentIdx + 1).toFloat() / total },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = SoyleAccent, trackColor = SoyleSurface2
            )
            Spacer(Modifier.weight(1f))

            // Подсказка
            Text(
                current.hint,
                fontSize = 12.sp, color = SoyleTextMuted,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            // Скороговорка с подсветкой Р
            Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp), Alignment.Center) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SoyleSurface)
                        .border(1.5.dp, SoyleBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    val annotated = buildAnnotatedString {
                        current.text.forEach { ch ->
                            if (ch.lowercaseChar() == 'р') withStyle(SpanStyle(color = SoyleAccent, fontWeight = FontWeight.ExtraBold)) { append(ch) }
                            else withStyle(SpanStyle(color = SoyleTextPrimary)) { append(ch) }
                        }
                    }
                    Text(annotated, fontSize = 20.sp, lineHeight = 30.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(28.dp))

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
                            else viewModel.startRecording(current.text.lowercase(), ExerciseMode.WORD)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isAnal) CircularProgressIndicator(color = SoyleAccent, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
                    else Icon(if (isRec) Icons.Outlined.Stop else Icons.Outlined.Mic, null,
                        tint = if (isRec) Color(0xFFE53935) else SoyleAccent, modifier = Modifier.size(32.dp))
                }
            }

            // Фидбек + мотивация
            AnimatedVisibility(visible = lastFeedback.isNotBlank(), modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                FeedbackBanner(feedback = lastFeedback, isSuccess = uiState is ExerciseUiState.Success && (uiState as ExerciseUiState.Success).score >= 50)
            }

            Spacer(Modifier.weight(1f))
            Text("${currentIdx + 1} / $total", fontSize = 12.sp, color = SoyleTextMuted,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), textAlign = TextAlign.Center)
        }
    }
}

@Composable
internal fun FeedbackBanner(feedback: String, isSuccess: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSuccess) Color(0xFF4CAF50).copy(0.12f) else Color(0xFFE53935).copy(0.10f))
            .border(1.dp, if (isSuccess) Color(0xFF4CAF50).copy(0.4f) else Color(0xFFE53935).copy(0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            if (isSuccess) Icons.Outlined.CheckCircle else Icons.Outlined.Info, null,
            tint = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFFF9800),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = if (feedback.isNotBlank()) feedback else if (isSuccess) "Отлично! Так держать!" else "Попробуй ещё раз",
            fontSize = 13.sp, color = SoyleTextSecondary, modifier = Modifier.weight(1f)
        )
    }
}
