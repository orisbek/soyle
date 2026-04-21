package com.example.soyle.ui.screens.game

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

data class ListenTask(
    val correctWord: String,
    val options: List<String>
)

val LISTEN_TASKS = listOf(
    ListenTask("РАК", listOf("ЛАК", "МАК", "РАК", "БАК")),
    ListenTask("РЫБА", listOf("ЛЫБА", "РЫБА", "ГЫБА", "ШЫБА")),
    ListenTask("РУКА", listOf("ЛУКА", "МУКА", "РУКА", "СУКА")),
    ListenTask("РОЗА", listOf("ЛОЗА", "КОЗА", "РОЗА", "ПОЗА")),
    ListenTask("ГОРА", listOf("ГОЛА", "ГОРА", "ГОДА", "ГОША")),
    ListenTask("РОТ", listOf("ЛОТ", "КОТ", "РОТ", "БОТ")),
    ListenTask("РОГ", listOf("ЛОГ", "ДОГ", "РОГ", "БОГ")),
    ListenTask("РИС", "ЛИС ТИС РИС КИС".split(" ")),
    ListenTask("РЕКА", "ЛЕКА ТЕКА РЕКА ЩЕКА".split(" ")),
    ListenTask("РЯД", "ЛЯД ГАД РЯД ЯД".split(" "))
)

@Composable
fun ListenChooseScreen(
    onBack: () -> Unit,
    onFinish: (Int) -> Unit
) {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showSuccess by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("Нажми на динамик и слушай слово!") }

    // Инициализация TTS
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru")
                tts?.speak(LISTEN_TASKS[0].correctWord, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val currentTask = LISTEN_TASKS[currentIndex]

    // Логика перехода при успехе
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(2000)
            if (currentIndex < LISTEN_TASKS.size - 1) {
                currentIndex++
                showSuccess = false
                feedbackText = "Нажми на динамик и слушай слово!"
                tts?.speak(LISTEN_TASKS[currentIndex].correctWord, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                // Итоговый балл (10 вопросов по 10%)
                onFinish(score)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(KidsBg)) {
        // Шапка
        Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(KidsPurple, KidsBlue))).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Text("←", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold) }
                Text("Слушай и выбирай", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))
                Text("${currentIndex + 1}/${LISTEN_TASKS.size}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier.weight(1f).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Маскот с текстом
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(KidsMintLight).border(2.dp, KidsMint.copy(0.4f), RoundedCornerShape(20.dp)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🦜", fontSize = 40.sp)
                Spacer(Modifier.width(12.dp))
                Text(feedbackText, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = KidsTextPrimary)
            }

            // Центральная кнопка звука
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(if (showSuccess) KidsMint else KidsBlue)
                    .clickable(!showSuccess) { 
                        tts?.speak(currentTask.correctWord, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (showSuccess) {
                    Text("🎉", fontSize = 60.sp)
                } else {
                    Icon(Icons.Rounded.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                }
            }

            // Сетка вариантов
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                currentTask.options.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        row.forEach { option ->
                            Card(
                                onClick = {
                                    if (!showSuccess) {
                                        if (option == currentTask.correctWord) {
                                            score += 10
                                            showSuccess = true
                                            feedbackText = "Верно! 🎉 100%"
                                            tts?.speak("Правильно", TextToSpeech.QUEUE_FLUSH, null, null)
                                        } else {
                                            feedbackText = "Ой! Это было слово $option. Попробуй еще раз! 🔊"
                                            tts?.speak("Неверно", TextToSpeech.QUEUE_FLUSH, null, null)
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(80.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (showSuccess && option == currentTask.correctWord) KidsMintLight else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(option, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = KidsTextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
