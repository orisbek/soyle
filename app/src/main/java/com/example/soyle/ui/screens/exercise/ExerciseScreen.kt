package com.example.soyle.ui.screens.exercise

import android.Manifest
import android.speech.tts.TextToSpeech
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
import com.example.soyle.ui.theme.*
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.random.Random

// Данные для режима "Слушай и выбирай"
data class ListenChooseTask(
    val correctWord: String,
    val options: List<String>
)

@Composable
fun ExerciseScreen(
    phoneme   : String,
    mode      : String,
    onResult  : (score: Int) -> Unit,
    onBack    : () -> Unit,
    viewModel : ExerciseViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsState()
    val exerciseMode  = remember { try { ExerciseMode.valueOf(mode) } catch(e: Exception) { ExerciseMode.SOUND } }
    val context       = LocalContext.current

    // Инициализация TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru")
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    // Список заданий
    val contentList = remember(exerciseMode) {
        when(exerciseMode) {
            ExerciseMode.SYLLABLE -> listOf("РА", "РО", "РУ", "РЫ", "РЕ")
            ExerciseMode.WORD -> listOf("РАК", "РЫБА", "РУКА", "РОЗА", "ГОРЫ")
            else -> listOf(phoneme)
        }
    }

    val listenChooseTasks = remember {
        listOf(
            ListenChooseTask("РАК", listOf("ЛАК", "МАК", "РАК", "БАК")),
            ListenChooseTask("РЫБА", listOf("ЛЫБА", "РЫБА", "ГЫБА", "ШЫБА")),
            ListenChooseTask("РУКА", listOf("ЛУКА", "МУКА", "РУКА", "СУКА")),
            ListenChooseTask("РОЗА", listOf("ЛОЗА", "КОЗА", "РОЗА", "ПОЗА")),
            ListenChooseTask("ГОРА", listOf("ГОЛА", "ГОРА", "ГОДА", "ГОША"))
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    
    // Функция озвучки
    val speak = { text: String ->
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Авто-озвучка при смене задания в режиме ListenChoose
    if (exerciseMode == ExerciseMode.LISTEN_CHOOSE) {
        LaunchedEffect(currentIndex) {
            delay(500)
            speak(listenChooseTasks[currentIndex].correctWord)
        }
    }

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasMicPermission = it }

    LaunchedEffect(uiState) {
        if (uiState is ExerciseUiState.Success) {
            val score = (uiState as ExerciseUiState.Success).score
            if (score >= 100) {
                delay(2000)
                if (exerciseMode == ExerciseMode.LISTEN_CHOOSE) {
                    if (currentIndex < listenChooseTasks.size - 1) {
                        currentIndex++
                        viewModel.reset()
                    } else onResult(100)
                } else {
                    if (currentIndex < contentList.size - 1) {
                        currentIndex++
                        viewModel.reset()
                    } else onResult(score)
                }
            } else if (score == 50) {
                delay(3500)
                viewModel.reset()
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val mascotY by infiniteTransition.animateFloat(
        initialValue  = 0f, targetValue   = -8f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "mascotBounce"
    )

    Column(modifier = Modifier.fillMaxSize().background(KidsBg)) {
        // Шапка
        Box(
            modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(KidsMint, KidsBlue))).padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Text("←", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Black) }
                Spacer(Modifier.width(8.dp))
                Text(text = modeTitle(exerciseMode, phoneme), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.weight(1f))
                val total = if (exerciseMode == ExerciseMode.LISTEN_CHOOSE) listenChooseTasks.size else contentList.size
                Text("${currentIndex + 1}/$total", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Маскот
            KidsExerciseMascot(
                text = when {
                    exerciseMode == ExerciseMode.LISTEN_CHOOSE -> "Послушай и выбери правильное слово!"
                    uiState is ExerciseUiState.Success -> (uiState as ExerciseUiState.Success).feedback
                    uiState is ExerciseUiState.Recording -> "Слушаю..."
                    else -> "Скажи «${contentList[currentIndex]}»! 🎙"
                },
                offsetY = mascotY
            )

            if (exerciseMode == ExerciseMode.LISTEN_CHOOSE) {
                // РЕЖИМ СЛУШАЙ И ВЫБИРАЙ
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    // Кнопка повтора звука
                    Button(
                        onClick = { speak(listenChooseTasks[currentIndex].correctWord) },
                        colors = ButtonDefaults.buttonColors(containerColor = KidsBlue),
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text("🔊", fontSize = 30.sp)
                    }

                    // Сетка 2x2
                    val task = listenChooseTasks[currentIndex]
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OptionCard(task.options[0], Modifier.weight(1f)) { checkAnswer(task.options[0], task.correctWord, viewModel) }
                            OptionCard(task.options[1], Modifier.weight(1f)) { checkAnswer(task.options[1], task.correctWord, viewModel) }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OptionCard(task.options[2], Modifier.weight(1f)) { checkAnswer(task.options[2], task.correctWord, viewModel) }
                            OptionCard(task.options[3], Modifier.weight(1f)) { checkAnswer(task.options[3], task.correctWord, viewModel) }
                        }
                    }
                }
            } else {
                // ОБЫЧНЫЙ РЕЖИМ (Звуки, Слоги, Слова)
                Box(
                    modifier = Modifier.size(220.dp).clip(RoundedCornerShape(32.dp)).background(Color.White).border(4.dp, KidsMint.copy(0.3f), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = contentList[currentIndex], fontSize = if (contentList[currentIndex].length > 2) 60.sp else 90.sp, fontWeight = FontWeight.Black, color = KidsMintDark, textAlign = TextAlign.Center)
                }
            }

            // Статус (только для режимов с микрофоном)
            if (exerciseMode != ExerciseMode.LISTEN_CHOOSE) {
                AnimatedContent(targetState = uiState, label = "status") { state ->
                    when (state) {
                        is ExerciseUiState.Idle -> Text("Нажми на кнопку и говори", color = KidsTextSecondary)
                        is ExerciseUiState.Recording -> RecordingIndicator()
                        is ExerciseUiState.Analyzing -> CircularProgressIndicator(color = KidsMint)
                        is ExerciseUiState.Success -> KidsSuccessResult(score = state.score)
                        is ExerciseUiState.Error -> Text(state.message, color = KidsPink)
                    }
                }
            } else if (uiState is ExerciseUiState.Success) {
                Text(if ((uiState as ExerciseUiState.Success).score == 100) "🎉 Верно!" else "❌ Попробуй еще раз", 
                    fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if ((uiState as ExerciseUiState.Success).score == 100) KidsMintDark else KidsPink)
            }
        }

        // Кнопка записи (не нужна для ListenChoose)
        if (exerciseMode != ExerciseMode.LISTEN_CHOOSE) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                if (!hasMicPermission) {
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }, colors = ButtonDefaults.buttonColors(containerColor = KidsMint)) {
                        Text("РАЗРЕШИТЬ МИКРОФОН", fontWeight = FontWeight.Bold)
                    }
                } else {
                    KidsRecordButton(
                        isRecording = uiState is ExerciseUiState.Recording,
                        enabled = uiState is ExerciseUiState.Idle || (uiState is ExerciseUiState.Success && (uiState as ExerciseUiState.Success).score < 100),
                        onClick = {
                            if (uiState is ExerciseUiState.Recording) viewModel.stopRecording()
                            else viewModel.startRecording(contentList[currentIndex], exerciseMode)
                        }
                    )
                }
            }
        }
    }
}

private fun checkAnswer(selected: String, correct: String, viewModel: ExerciseViewModel) {
    if (selected == correct) {
        // Имитируем успех для ViewModel
        viewModel.startRecording("", ExerciseMode.LISTEN_CHOOSE) // This is a hack to trigger logic, ideally ViewModel should have a dedicated checkAnswer
    }
}

@Composable
fun OptionCard(text: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = KidsTextPrimary)
        }
    }
}

@Composable
private fun KidsExerciseMascot(text: String, offsetY: Float) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(KidsMintLight).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🦜", fontSize = 44.sp, modifier = Modifier.offset(y = offsetY.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = KidsTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun RecordingIndicator() {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i ->
            val alpha by rememberInfiniteTransition().animateFloat(
                initialValue = 0.2f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(600, delayMillis = i * 200), RepeatMode.Reverse)
            )
            Box(Modifier.size(10.dp).clip(CircleShape).background(KidsPink.copy(alpha = alpha)))
        }
    }
}

@Composable
private fun KidsSuccessResult(score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (score >= 100) "🎉 ИДЕАЛЬНО!" else "👍 ХОРОШО!", fontWeight = FontWeight.Black, color = if (score >= 100) KidsMintDark else KidsOrange)
        Text("Точность: $score%", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun KidsRecordButton(isRecording: Boolean, enabled: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(80.dp).clip(CircleShape).background(if (isRecording) KidsPink else if (enabled) KidsMint else Color.Gray)
    ) {
        Text(if (isRecording) "⏹" else "🎙", fontSize = 36.sp, color = Color.White)
    }
}

private fun modeTitle(mode: ExerciseMode, phoneme: String) = when (mode) {
    ExerciseMode.SOUND -> "Звук «$phoneme»"
    ExerciseMode.SYLLABLE -> "Слоги с «$phoneme»"
    ExerciseMode.WORD -> "Слова с «$phoneme»"
    ExerciseMode.LISTEN_CHOOSE -> "Слушай и выбирай"
    ExerciseMode.GAME -> "Игра"
    else -> "Упражнение"
}
