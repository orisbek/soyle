package com.example.soyle.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Модели данных ─────────────────────────────────────────────────────────────

data class Practice(
    val id      : String,
    val icon    : ImageVector,
    val title   : String,
    val duration: String,
    val isNew   : Boolean  = false,
    val isLocked: Boolean  = false
)

data class DailyTip(
    val text   : String,
    val author : String,
    val source : String
)

// ── Главный экран ─────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onOpenCheckIn  : () -> Unit = {},
    onOpenExercise : (String) -> Unit = {},
    onOpenProfile  : () -> Unit = {},
    viewModel      : HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val streak         = uiState.currentStreak
    val checkedInToday = uiState.currentStreak > 0
    val weekDays       = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    // Заполняем последние N дней по серии (упрощённо)
    val checkedCount   = uiState.weekCheckedCount
    val checkedDays    = (0 until checkedCount).toSet()

    val hour = remember {
        java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    }
    val greeting = when {
        hour < 12 -> "доброе утро."
        hour < 17 -> "добрый день."
        else      -> "добрый вечер."
    }

    val practices = remember {
        listOf(
            Practice("1", Icons.Outlined.Mood,             "Проверка\nнастроения",   "2 мин"),
            Practice("2", Icons.Outlined.Air,              "Упражнение\nс дыханием", "5 мин"),
            Practice("3", Icons.Outlined.RecordVoiceOver,  "Звук «Р»\nпо слогам",   "7 мин"),
            Practice("4", Icons.Outlined.EditNote,         "Дневник\nречи",          "3 мин"),
            Practice("5", Icons.Outlined.Speed,            "Скороговорки",           "5 мин", isNew = true),
            Practice("6", Icons.Outlined.Summarize,        "Итог дня",               "3 мин")
        )
    }

    val tip = remember {
        DailyTip(
            text   = "Регулярные короткие занятия дают лучший результат, чем редкие длинные.",
            author = "Принцип логопедии",
            source = ""
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Шапка ─────────────────────────────────────────────────────────
        HomeTopBar(
            streak    = streak,
            greeting  = greeting,
            onProfile = onOpenProfile
        )

        Column(
            modifier            = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── Блок чек-ина ──────────────────────────────────────────────
            Spacer(Modifier.height(20.dp))
            CheckInCard(
                checked    = checkedInToday,
                weekDays   = weekDays,
                checkedSet = checkedDays,
                onClick    = onOpenCheckIn
            )

            // ── Твои практики ─────────────────────────────────────────────
            Spacer(Modifier.height(28.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Твои практики",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 17.sp,
                    color      = SoyleTextPrimary
                )
                Icon(
                    imageVector        = Icons.Outlined.Settings,
                    contentDescription = "Настройки",
                    tint               = SoyleTextSecondary,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text     = "Упражнения выбраны для твоего прогресса. Добавляй свои!",
                fontSize = 13.sp,
                color    = SoyleTextSecondary,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(16.dp))

            // Горизонтальный скролл практик
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding        = PaddingValues(end = 8.dp)
            ) {
                items(practices) { practice ->
                    PracticeCard(
                        practice = practice,
                        onClick  = { onOpenExercise(practice.id) }
                    )
                }
            }

            // ── Совет дня ────────────────────────────────────────────────
            Spacer(Modifier.height(28.dp))
            DailyTipCard(tip = tip)

            // ── Статистика недели ─────────────────────────────────────────
            Spacer(Modifier.height(28.dp))
            WeeklyStats(
                streak       = streak,
                checkedDays  = checkedDays,
                avgScore     = uiState.avgScore
            )

            Spacer(Modifier.height(100.dp))
        }
    }
}

// ── Шапка ────────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(streak: Int, greeting: String, onProfile: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Серия (streak) — огонь только если streak > 0
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SoyleSurface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (streak > 0) {
                    Icon(
                        imageVector        = Icons.Outlined.Whatshot,
                        contentDescription = "Серия",
                        tint               = SoyleStreak,
                        modifier           = Modifier.size(14.dp)
                    )
                    Text(
                        text       = "$streak",
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color      = SoyleStreak
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Outlined.Whatshot,
                        contentDescription = "Серия",
                        tint               = SoyleTextMuted,
                        modifier           = Modifier.size(14.dp)
                    )
                    Text(
                        text     = "0",
                        fontSize = 10.sp,
                        color    = SoyleTextMuted
                    )
                }
            }
        }

        // Приветствие
        Text(
            text       = greeting,
            fontWeight = FontWeight.Normal,
            fontSize   = 16.sp,
            color      = SoyleTextPrimary
        )

        // Аватар
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SoyleSurface)
                .border(1.dp, SoyleBorder, CircleShape)
                .clickable(onClick = onProfile),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Person,
                contentDescription = "Профиль",
                tint               = SoyleTextSecondary,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}

// ── Карточка чек-ина ─────────────────────────────────────────────────────────

@Composable
private fun CheckInCard(
    checked    : Boolean,
    weekDays   : List<String>,
    checkedSet : Set<Int>,
    onClick    : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(18.dp))
            .clickable(enabled = !checked, onClick = onClick)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (checked) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Чек-ин выполнен.",
                    fontSize = 16.sp,
                    color    = SoyleTextMuted
                )
                SoyleTag(text = "Активен")
            }
        } else {
            Text(
                text       = "Начни чек-ин",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 17.sp,
                color      = SoyleTextPrimary
            )
            Text(
                text     = "Как чувствуешь себя сегодня?",
                fontSize = 14.sp,
                color    = SoyleTextSecondary
            )
        }

        // Мини-календарь недели
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEachIndexed { index, day ->
                WeekDayDot(day = day, isActive = index in checkedSet, isToday = index == 0)
            }
        }
    }
}

@Composable
private fun WeekDayDot(day: String, isActive: Boolean, isToday: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isActive -> SoyleTextPrimary
                        isToday  -> SoyleSurface2
                        else     -> Color.Transparent
                    }
                )
                .border(
                    1.dp,
                    if (isToday && !isActive) SoyleBorderLight else Color.Transparent,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                Icon(
                    imageVector        = Icons.Outlined.Whatshot,
                    contentDescription = null,
                    tint               = SoyleBg,
                    modifier           = Modifier.size(14.dp)
                )
            }
        }
        Text(
            text     = day,
            fontSize = 10.sp,
            color    = if (isToday) SoyleTextSecondary else SoyleTextMuted
        )
    }
}

// ── Карточка практики ─────────────────────────────────────────────────────────

@Composable
private fun PracticeCard(practice: Practice, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(18.dp))
            .clickable(enabled = !practice.isLocked, onClick = onClick)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SoyleSurface2),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = practice.icon,
                contentDescription = practice.title,
                tint               = if (practice.isLocked) SoyleTextMuted else SoyleAccent,
                modifier           = Modifier.size(24.dp)
            )
        }

        if (practice.isNew) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SoyleAccentSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = "НОВОЕ",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color    = SoyleAccentLight,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Text(
            text      = practice.title,
            fontSize  = 11.sp,
            color     = if (practice.isLocked) SoyleTextMuted else SoyleTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
    }
}

// ── Совет дня ────────────────────────────────────────────────────────────────

@Composable
private fun DailyTipCard(tip: DailyTip) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(18.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text       = "““",
                fontSize   = 28.sp,
                color      = SoyleTextMuted,
                lineHeight = 0.sp
            )
            Text(
                text      = tip.text,
                fontSize  = 17.sp,
                color     = SoyleTextPrimary,
                fontWeight = FontWeight.Normal,
                lineHeight = 26.sp
            )
            if (tip.author.isNotEmpty()) {
                Text(
                    text     = tip.author,
                    fontSize = 13.sp,
                    color    = SoyleTextSecondary
                )
            }
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text     = "Записать мысли",
                    fontSize = 14.sp,
                    color    = SoyleTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector        = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint               = SoyleTextMuted,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Статистика недели ─────────────────────────────────────────────────────────

@Composable
private fun WeeklyStats(streak: Int, checkedDays: Set<Int>, avgScore: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(18.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text       = "На этой неделе",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                color      = SoyleTextPrimary
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(value = "${checkedDays.size}", label = "занятий")
                StatItem(
                    value      = "$streak",
                    label      = "серия",
                    showFire   = streak > 0
                )
                StatItem(
                    value = if (avgScore > 0) "$avgScore%" else "—",
                    label = "ср. оценка"
                )
            }
            if (checkedDays.size < 3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SoyleSurface2)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text     = "Ещё ${3 - checkedDays.size} дня до первого графика",
                            fontSize = 13.sp,
                            color    = SoyleTextSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(3) { i ->
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (i < checkedDays.size) SoyleTextPrimary else SoyleSurface
                                        )
                                        .border(
                                            1.dp,
                                            if (i < checkedDays.size) Color.Transparent else SoyleBorderLight,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (i < checkedDays.size) {
                                        Icon(
                                            imageVector        = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint               = SoyleBg,
                                            modifier           = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, showFire: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (showFire) {
                Icon(
                    imageVector        = Icons.Outlined.Whatshot,
                    contentDescription = null,
                    tint               = SoyleStreak,
                    modifier           = Modifier.size(16.dp)
                )
            }
            Text(
                text       = value,
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = if (showFire) SoyleStreak else SoyleTextPrimary
            )
        }
        Text(
            text     = label,
            fontSize = 11.sp,
            color    = SoyleTextSecondary
        )
    }
}
