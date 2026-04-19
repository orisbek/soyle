package com.example.soyle.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.Exercise
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.ui.components.DuoChoiceCard
import com.example.soyle.ui.components.DuoMascotSpeech
import com.example.soyle.ui.components.DuoProgressBar
import com.example.soyle.ui.components.DuoSectionHeader
import com.example.soyle.ui.components.DuoLevelBadge
import com.example.soyle.ui.components.DuoStreakBadge
import com.example.soyle.ui.components.DuoXpBadge
import com.example.soyle.ui.theme.*

@Composable
fun HomeScreen(
    onStartExercise: (phoneme: String, mode: String) -> Unit,
    onOpenProgress: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DuoBg,
        bottomBar = {
            NavigationBar(containerColor = DuoWhite, tonalElevation = 0.dp) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Главная") }, colors = NavigationBarItemDefaults.colors(indicatorColor = DuoGreenLight, selectedIconColor = DuoGreen))
                NavigationBarItem(selected = false, onClick = onOpenProgress, icon = { Icon(Icons.Default.BarChart, null) }, label = { Text("Прогресс") })
                NavigationBarItem(selected = false, onClick = onOpenProfile, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Профиль") })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            HomeTopBar(uiState.currentStreak, uiState.totalXp, uiState.level)
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Spacer(Modifier.height(4.dp))
                DuoMascotSpeech(text = uiState.greeting, mascotEmoji = "S")
                DailyGoalCard(done = uiState.todayDone, total = uiState.todayTotal)

                DuoSectionHeader(title = "Сегодняшние упражнения")
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) { CircularProgressIndicator(color = DuoGreen) }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(end = 4.dp)) {
                        items(uiState.exercises.take(6)) { exercise ->
                            ExerciseCard(exercise = exercise, onClick = { onStartExercise(exercise.phoneme, exercise.mode.name) })
                        }
                    }
                }

                DuoSectionHeader(title = "Игры")
                QuickStartList(onStartExercise)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HomeTopBar(streak: Int, xp: Int, level: Int) {
    Row(modifier = Modifier.fillMaxWidth().background(DuoWhite).padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Soyle", fontWeight = FontWeight.Black, fontSize = 22.sp, color = DuoGreen)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            DuoStreakBadge(streak)
            DuoXpBadge(xp)
            DuoLevelBadge(level)
        }
    }
    HorizontalDivider(color = DuoBorder, thickness = 1.dp)
}

@Composable
private fun DailyGoalCard(done: Int, total: Int) {
    val progress = if (total > 0) done.toFloat() / total else 0f
    Column(
        modifier = Modifier.fillMaxWidth().background(DuoGreenPale, RoundedCornerShape(16.dp)).border(2.dp, DuoGreenLight, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Дневная цель", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = DuoTextPrimary)
            Text("$done / $total", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DuoGreen)
        }
        DuoProgressBar(progress = progress)
    }
}

@Composable
private fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(120.dp).border(2.dp, DuoBorder, RoundedCornerShape(16.dp)).background(DuoWhite, RoundedCornerShape(16.dp)).clickable { onClick() }.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(modeLabel(exercise.mode), fontWeight = FontWeight.Bold, color = DuoTextSecondary, fontSize = 11.sp)
        Text(text = exercise.content, fontWeight = FontWeight.Black, fontSize = 20.sp, color = DuoGreen)
        Text(text = "Сложность ${exercise.difficulty}", fontSize = 10.sp, color = DuoTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun QuickStartList(onStartExercise: (String, String) -> Unit) {
    val modes = listOf(
        ExerciseMode.GAME_RHYTHM to "Ритм-слоги",
        ExerciseMode.GAME_ECHO to "Эхо-фразы",
        ExerciseMode.GAME_PUZZLE to "Слово-пазл"
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        modes.forEach { (mode, label) ->
            DuoChoiceCard(text = label, modifier = Modifier.fillMaxWidth(), onClick = { onStartExercise("Р", mode.name) })
        }
    }
}

private fun modeLabel(mode: ExerciseMode) = when (mode) {
    ExerciseMode.SOUND -> "Звук"
    ExerciseMode.SYLLABLE -> "Слог"
    ExerciseMode.WORD -> "Слово"
    ExerciseMode.LISTEN_CHOOSE -> "Слушай"
    ExerciseMode.VISUALIZE -> "Ритм"
    ExerciseMode.GAME, ExerciseMode.GAME_RHYTHM, ExerciseMode.GAME_ECHO, ExerciseMode.GAME_PUZZLE -> "Игра"
}
