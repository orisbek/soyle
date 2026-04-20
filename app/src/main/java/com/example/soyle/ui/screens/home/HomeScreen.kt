package com.example.soyle.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        containerColor = KidsBg,
        bottomBar = {
            KidsBottomBar(
                onHome     = {},
                onProgress = onOpenProgress,
                onProfile  = onOpenProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Красочная шапка с облачком ────────────────────────────────
            KidsTopBar(
                streak    = uiState.currentStreak,
                xp        = uiState.totalXp,
                level     = uiState.level,
                onProfile = onOpenProfile
            )

            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // ── Маскот с приветствием ──────────────────────────────────
                KidsMascotCard(text = uiState.greeting)

                // ── Дневная цель ───────────────────────────────────────────
                KidsDailyGoal(done = uiState.todayDone, total = uiState.todayTotal)

                // ── Упражнения сегодня ─────────────────────────────────────
                KidsSectionTitle(title = "✨ Сегодня учим", emoji = "")

                if (uiState.isLoading) {
                    Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) {
                        CircularProgressIndicator(color = KidsMint, modifier = Modifier.size(40.dp))
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding        = PaddingValues(end = 4.dp)
                    ) {
                        items(uiState.exercises.take(6)) { exercise ->
                            KidsExerciseCard(
                                exercise = exercise,
                                onClick  = { onStartExercise(exercise.phoneme, exercise.mode.name) }
                            )
                        }
                    }
                }

                // ── Все режимы ─────────────────────────────────────────────
                KidsSectionTitle(title = "🎯 Все занятия", emoji = "")
                KidsModeList(onStartExercise = onStartExercise)

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// ── Детская шапка с градиентом ────────────────────────────────────────────────

@Composable
private fun KidsTopBar(
    streak   : Int,
    xp       : Int,
    level    : Int,
    onProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(KidsMint, KidsBlue)
                )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Лого
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🦜", fontSize = 28.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Söyle",
                    fontWeight = FontWeight.Black,
                    fontSize   = 26.sp,
                    color      = Color.White
                )
            }
            // Бейджи
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                KidsBadge(icon = "🔥", value = streak.toString(), color = KidsOrange)
                KidsBadge(icon = "⭐", value = xp.toString(), color = KidsYellow)
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .clickable(onClick = onProfile)
                        .padding(8.dp)
                ) {
                    Text("👤", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun KidsBadge(icon: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 16.sp)
        Text(
            text       = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 14.sp,
            color      = Color.White
        )
    }
}

// ── Маскот-карточка ───────────────────────────────────────────────────────────

@Composable
private fun KidsMascotCard(text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotBounce"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(KidsMintLight, KidsBlueLight),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .border(3.dp, KidsMint.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Маскот — парrot
        Text(
            text     = "🦜",
            fontSize = 52.sp,
            modifier = Modifier.offset(y = offsetY.dp)
        )
        Column {
            Text(
                text       = "Привет!",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 13.sp,
                color      = KidsMintDark
            )
            Text(
                text       = text,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                color      = KidsTextPrimary,
                lineHeight = 21.sp
            )
        }
    }
}

// ── Дневная цель ──────────────────────────────────────────────────────────────

@Composable
private fun KidsDailyGoal(done: Int, total: Int) {
    val progress = if (total > 0) done.toFloat() / total else 0f
    val stars    = when {
        progress >= 1f   -> 3
        progress >= 0.6f -> 2
        progress >= 0.3f -> 1
        else             -> 0
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(KidsYellowLight)
            .border(3.dp, KidsYellow, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎯", fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Дневная цель",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 16.sp,
                    color      = KidsTextPrimary
                )
            }
            // Звёздочки прогресса
            Row {
                repeat(3) { i ->
                    Text(
                        text     = if (i < stars) "⭐" else "☆",
                        fontSize = 20.sp
                    )
                }
            }
        }

        // Прогресс-бар толстый и красивый
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(KidsYellow, KidsOrange)
                        )
                    )
            )
        }

        Text(
            text       = "$done из $total занятий выполнено 🎉",
            fontWeight = FontWeight.Bold,
            fontSize   = 13.sp,
            color      = KidsTextSecondary
        )
    }
}

// ── Заголовок секции ──────────────────────────────────────────────────────────

@Composable
private fun KidsSectionTitle(title: String, emoji: String) {
    Text(
        text       = title,
        fontWeight = FontWeight.ExtraBold,
        fontSize   = 18.sp,
        color      = KidsTextPrimary
    )
}

// ── Карточка упражнения ───────────────────────────────────────────────────────

@Composable
private fun KidsExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    val colors = listOf(
        Pair(KidsMintLight, KidsMint),
        Pair(KidsBlueLight, KidsBlue),
        Pair(KidsPinkLight, KidsPink),
        Pair(KidsYellowLight, KidsYellow),
        Pair(KidsGreenLight, KidsGreen),
    )
    val (bg, border) = colors[exercise.mode.ordinal % colors.size]

    Column(
        modifier = Modifier
            .width(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(3.dp, border, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(modeEmoji(exercise.mode), fontSize = 32.sp)
        Text(
            text       = exercise.content,
            fontWeight = FontWeight.Black,
            fontSize   = 22.sp,
            color      = border
        )
        Text(
            text     = modeLabel(exercise.mode),
            fontSize = 10.sp,
            color    = KidsTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Список режимов (красивые карточки) ───────────────────────────────────────

@Composable
private fun KidsModeList(onStartExercise: (String, String) -> Unit) {
    val modes = listOf(
        ModeItem(ExerciseMode.SOUND,         "Звук «Р»",        "🔤", KidsMint,   KidsMintLight),
        ModeItem(ExerciseMode.SYLLABLE,      "Слоги с Р",       "📝", KidsBlue,   KidsBlueLight),
        ModeItem(ExerciseMode.WORD,          "Слова с Р",       "🗣️", KidsPurple, Color(0xFFEDE7FF)),
        ModeItem(ExerciseMode.LISTEN_CHOOSE, "Слушай и выбирай","👂", KidsOrange, Color(0xFFFFEDD5)),
        ModeItem(ExerciseMode.VISUALIZE,     "Волна звука",     "🌊", KidsGreen,  KidsGreenLight),
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        modes.forEach { item ->
            KidsModeCard(item = item) {
                onStartExercise("Р", item.mode.name)
            }
        }

        // Секция игр
        Spacer(Modifier.height(4.dp))
        Text(
            text       = "🎮 Игры",
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 18.sp,
            color      = KidsTextPrimary
        )

        // Поймай букву
        KidsModeCard(
            item = ModeItem(ExerciseMode.GAME, "Поймай букву «Р»!", "🎯", KidsPink, KidsPinkLight)
        ) { onStartExercise("Р", "GAME") }

        // Собери слово
        KidsModeCard(
            item = ModeItem(ExerciseMode.WORD, "Собери слово!  (50 уровней)", "🔤", KidsOrange, Color(0xFFFFEDD5))
        ) { onStartExercise("Р", "WORD_BUILDING") }
    }
}

private data class ModeItem(
    val mode       : ExerciseMode,
    val label      : String,
    val emoji      : String,
    val color      : Color,
    val bgColor    : Color
)

@Composable
private fun KidsModeCard(item: ModeItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(item.bgColor)
            .border(3.dp, item.color.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(item.color),
            contentAlignment = Alignment.Center
        ) {
            Text(item.emoji, fontSize = 26.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = item.label,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 16.sp,
                color      = KidsTextPrimary
            )
        }
        // Стрелочка
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(item.color),
            contentAlignment = Alignment.Center
        ) {
            Text("→", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Black)
        }
    }
}

// ── Нижняя навигация ─────────────────────────────────────────────────────────

@Composable
private fun KidsBottomBar(
    onHome    : () -> Unit,
    onProgress: () -> Unit,
    onProfile : () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier       = Modifier.border(
            width = 2.dp,
            color = KidsBorder,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        )
    ) {
        NavigationBarItem(
            selected = true,
            onClick  = onHome,
            icon     = { Text("🏠", fontSize = 22.sp) },
            label    = { Text("Главная", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors   = NavigationBarItemDefaults.colors(
                indicatorColor    = KidsMintLight,
                selectedIconColor = KidsMint
            )
        )
        NavigationBarItem(
            selected = false,
            onClick  = onProgress,
            icon     = { Text("📊", fontSize = 22.sp) },
            label    = { Text("Прогресс", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick  = onProfile,
            icon     = { Text("👦", fontSize = 22.sp) },
            label    = { Text("Профиль", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
        )
    }
}

private fun modeEmoji(mode: ExerciseMode) = when (mode) {
    ExerciseMode.SOUND         -> "🔤"
    ExerciseMode.SYLLABLE      -> "📝"
    ExerciseMode.WORD          -> "🗣️"
    ExerciseMode.LISTEN_CHOOSE -> "👂"
    ExerciseMode.VISUALIZE     -> "🌊"
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
