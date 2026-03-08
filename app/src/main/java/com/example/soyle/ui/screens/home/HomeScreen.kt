package com.example.soyle.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.Exercise
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

@Composable
fun HomeScreen(
    onStartExercise : (phoneme: String, mode: String) -> Unit,
    onOpenProgress  : () -> Unit,
    onOpenProfile   : () -> Unit,
    viewModel       : HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DuoBg,
        bottomBar = {
            NavigationBar(
                containerColor = DuoWhite,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick  = {},
                    icon     = { Icon(Icons.Default.Home, null) },
                    label    = { Text("Главная", fontWeight = FontWeight.Bold) },
                    colors   = NavigationBarItemDefaults.colors(
                        indicatorColor = DuoGreenLight,
                        selectedIconColor = DuoGreen
                    )
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
                .verticalScroll(rememberScrollState())
        ) {
            // ── Шапка ─────────────────────────────────────────────────────
            HomeTopBar(
                streak   = uiState.currentStreak,
                xp       = uiState.totalXp,
                level    = uiState.level,
                onProfile = onOpenProfile
            )

            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // ── Маскот-приветствие ─────────────────────────────────────
                DuoMascotSpeech(text = uiState.greeting)

                // ── Дневная цель ───────────────────────────────────────────
                DailyGoalCard(done = uiState.todayDone, total = uiState.todayTotal)

                // ── Сегодняшние упражнения ─────────────────────────────────
                DuoSectionHeader(title = "Сегодняшние упражнения")

                if (uiState.isLoading) {
                    Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) {
                        CircularProgressIndicator(color = DuoGreen)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding        = PaddingValues(end = 4.dp)
                    ) {
                        items(uiState.exercises.take(6)) { exercise ->
                            ExerciseCard(
                                exercise = exercise,
                                onClick  = { onStartExercise(exercise.phoneme, exercise.mode.name) }
                            )
                        }
                    }
                }

                // ── Быстрый старт ──────────────────────────────────────────
                DuoSectionHeader(title = "Все режимы")
                QuickStartList(onStartExercise = onStartExercise)

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Шапка ─────────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(
    streak   : Int,
    xp       : Int,
    level    : Int,
    onProfile: () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .background(DuoWhite)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Логотип / название
        Text(
            text       = "Söyle",
            fontWeight = FontWeight.Black,
            fontSize   = 22.sp,
            color      = DuoGreen
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            DuoStreakBadge(streak = streak)
            DuoXpBadge(xp = xp)
            DuoLevelBadge(level = level)
        }
    }
    HorizontalDivider(color = DuoBorder, thickness = 1.dp)
}

// ── Дневная цель ──────────────────────────────────────────────────────────────

@Composable
private fun DailyGoalCard(done: Int, total: Int) {
    val progress = if (total > 0) done.toFloat() / total else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DuoGreenPale, RoundedCornerShape(16.dp))
            .border(2.dp, DuoGreenLight, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎯", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Дневная цель",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 15.sp,
                    color      = DuoTextPrimary
                )
            }
            Text(
                text       = "$done / $total упражнений",
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = DuoGreen
            )
        }
        DuoProgressBar(progress = progress)
    }
}

// ── Карточка упражнения ───────────────────────────────────────────────────────

@Composable
private fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, DuoBorder, RoundedCornerShape(16.dp))
            .background(DuoWhite)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(modeEmoji(exercise.mode), fontSize = 28.sp)
        Text(
            text       = exercise.content,
            fontWeight = FontWeight.Black,
            fontSize   = 20.sp,
            color      = DuoGreen
        )
        Text(
            text     = modeLabel(exercise.mode),
            fontSize = 10.sp,
            color    = DuoTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ── Список режимов ────────────────────────────────────────────────────────────

@Composable
private fun QuickStartList(onStartExercise: (String, String) -> Unit) {
    val modes = listOf(
        Triple(ExerciseMode.SOUND,        "Звук «Р»",    "🔤"),
        Triple(ExerciseMode.SYLLABLE,     "Слоги с Р",   "📝"),
        Triple(ExerciseMode.WORD,         "Слова с Р",   "🗣️"),
        Triple(ExerciseMode.LISTEN_CHOOSE,"Слушай",       "👂"),
        Triple(ExerciseMode.VISUALIZE,    "Визуализация", "📊"),
        Triple(ExerciseMode.GAME,         "Игра",         "🎮"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        modes.forEach { (mode, label, emoji) ->
            DuoChoiceCard(
                text     = label,
                emoji    = emoji,
                modifier = Modifier.fillMaxWidth(),
                onClick  = { onStartExercise("Р", mode.name) }
            )
        }
    }
}

private fun modeEmoji(mode: ExerciseMode) = when (mode) {
    ExerciseMode.SOUND         -> "🔤"
    ExerciseMode.SYLLABLE      -> "📝"
    ExerciseMode.WORD          -> "🗣️"
    ExerciseMode.LISTEN_CHOOSE -> "👂"
    ExerciseMode.VISUALIZE     -> "📊"
    ExerciseMode.GAME          -> "🎮"
}

private fun modeLabel(mode: ExerciseMode) = when (mode) {
    ExerciseMode.SOUND         -> "Звук"
    ExerciseMode.SYLLABLE      -> "Слог"
    ExerciseMode.WORD          -> "Слово"
    ExerciseMode.LISTEN_CHOOSE -> "Слушай"
    ExerciseMode.VISUALIZE     -> "Волна"
    ExerciseMode.GAME          -> "Игра"
}