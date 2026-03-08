package com.example.soyle.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.domain.model.ExerciseMode

@Composable
fun HomeScreen(
    onStartExercise : (phoneme: String, mode: String) -> Unit,
    onOpenProgress  : () -> Unit,
    onOpenProfile   : () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick  = {},
                    icon     = { Icon(Icons.Default.Home, null) },
                    label    = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick  = onOpenProgress,
                    icon     = { Icon(Icons.Default.BarChart, null) },
                    label    = { Text("Прогресс") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick  = onOpenProfile,
                    icon     = { Icon(Icons.Default.Person, null) },
                    label    = { Text("Профиль") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Маскот
            Text("🦉", fontSize = 72.sp)
            Text(
                text       = "Привет! Пора тренироваться!",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Streak карточка
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Серия дней", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("0 дней", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Кнопки упражнений
            Text("Выбери упражнение:", fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Button(
                onClick  = { onStartExercise("Р", ExerciseMode.SOUND.name) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("🔤  Звук «Р»", fontSize = 16.sp)
            }

            OutlinedButton(
                onClick  = { onStartExercise("Р", ExerciseMode.SYLLABLE.name) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("🔤  Слоги с «Р»", fontSize = 16.sp)
            }

            OutlinedButton(
                onClick  = { onStartExercise("Р", ExerciseMode.WORD.name) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("🔤  Слова с «Р»", fontSize = 16.sp)
            }
        }
    }
}