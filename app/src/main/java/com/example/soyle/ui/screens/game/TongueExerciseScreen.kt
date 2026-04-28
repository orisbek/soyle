package com.example.soyle.ui.screens.game

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.soyle.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TongueExerciseScreen(onBack: () -> Unit = {}) {
    val exercises = remember { TongueExercisesData.all }
    var currentIdx  by remember { mutableIntStateOf(0) }
    var isRunning   by remember { mutableStateOf(false) }
    var timeLeft    by remember { mutableIntStateOf(0) }
    var isDone      by remember { mutableStateOf(false) }
    var allDone     by remember { mutableStateOf(false) }

    val current = if (currentIdx < exercises.size) exercises[currentIdx] else null

    // Таймер
    LaunchedEffect(isRunning, currentIdx) {
        if (!isRunning || current == null) return@LaunchedEffect
        timeLeft = current.durationSec
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        isRunning = false
        isDone = true
    }

    fun next() {
        isDone = false
        if (currentIdx + 1 < exercises.size) { currentIdx++; timeLeft = 0 }
        else allDone = true
    }

    fun restart() { currentIdx = 0; isRunning = false; isDone = false; allDone = false; timeLeft = 0 }

    when {
        allDone -> ExerciseCompleteScreen(total = exercises.size, onBack = onBack, onRestart = ::restart)
        current == null -> Box(Modifier.fillMaxSize().background(SoyleBg), Alignment.Center) {
            Text("Нет упражнений", color = SoyleTextSecondary)
        }
        else -> Column(Modifier.fillMaxSize().background(SoyleBg)) {
            // Верхняя панель
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Назад", tint = SoyleTextPrimary)
                }
                Text("Гимнастика языка", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = SoyleTextPrimary, modifier = Modifier.weight(1f).padding(start = 4.dp))
                Text("${currentIdx + 1}/${exercises.size}", fontSize = 13.sp, color = SoyleTextMuted)
            }

            LinearProgressIndicator(
                progress = { (currentIdx + 1).toFloat() / exercises.size },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = SoyleAccent, trackColor = SoyleSurface2
            )

            Spacer(Modifier.weight(1f))

            // Эмодзи + название
            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(100.dp).clip(CircleShape)
                        .background(SoyleAccentSoft)
                        .border(2.dp, SoyleAccent.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(current.emoji, fontSize = 44.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                current.name,
                fontWeight = FontWeight.Bold, fontSize = 26.sp, color = SoyleTextPrimary,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )
            Text(
                current.description,
                fontSize = 14.sp, color = SoyleTextMuted,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Инструкция
            Box(
                modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoyleSurface)
                    .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    current.instruction,
                    fontSize = 15.sp, color = SoyleTextPrimary, lineHeight = 24.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(32.dp))

            // Таймер / кнопка / результат
            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                when {
                    isDone -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(
                                modifier = Modifier.size(80.dp).clip(CircleShape)
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f))
                                    .border(2.dp, Color(0xFF4CAF50), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(36.dp))
                            }
                            Text("Молодец! Упражнение выполнено!", fontSize = 15.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
                            Button(
                                onClick = ::next,
                                colors  = ButtonDefaults.buttonColors(containerColor = SoyleAccent),
                                shape   = RoundedCornerShape(14.dp),
                                modifier = Modifier.padding(horizontal = 32.dp)
                            ) {
                                Text(if (currentIdx + 1 < exercises.size) "Следующее →" else "Завершить", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    isRunning -> {
                        // Пульсирующий таймер
                        val scale by rememberInfiniteTransition(label = "t").animateFloat(
                            0.95f, 1.05f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "ts"
                        )
                        Box(
                            modifier = Modifier
                                .size(100.dp).clip(CircleShape)
                                .background(SoyleAccent.copy(alpha = 0.15f))
                                .border(3.dp, SoyleAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$timeLeft", fontWeight = FontWeight.Bold, fontSize = 36.sp, color = SoyleAccent)
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .size(90.dp).clip(CircleShape)
                                .background(SoyleAccentSoft)
                                .border(2.dp, SoyleAccent, CircleShape)
                                .clickable { isRunning = true; isDone = false; timeLeft = current.durationSec },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Outlined.PlayArrow, "Старт", tint = SoyleAccent, modifier = Modifier.size(32.dp))
                                Text("Старт", fontSize = 12.sp, color = SoyleAccent, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun ExerciseCompleteScreen(total: Int, onBack: () -> Unit, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(SoyleBg).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉", fontSize = 64.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Text("Гимнастика завершена!", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = SoyleTextPrimary, textAlign = TextAlign.Center)
        Text("Ты выполнил все $total упражнений", fontSize = 15.sp, color = SoyleTextSecondary, modifier = Modifier.padding(top = 8.dp), textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRestart,
            colors  = ButtonDefaults.buttonColors(containerColor = SoyleAccent),
            shape   = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Повторить ещё раз", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp)) }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            shape   = RoundedCornerShape(14.dp),
            border  = BorderStroke(1.dp, SoyleBorder),
            modifier = Modifier.fillMaxWidth()
        ) { Text("На главную", color = SoyleTextSecondary, modifier = Modifier.padding(vertical = 4.dp)) }
    }
}
