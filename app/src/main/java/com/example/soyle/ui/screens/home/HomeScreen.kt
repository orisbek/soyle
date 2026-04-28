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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
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
    val color   : Color    = Color(0xFF6C63FF)
)

// ── Главный экран ─────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onOpenCheckIn  : () -> Unit = {},
    onOpenExercise : (String) -> Unit = {},
    onOpenGame     : (String) -> Unit = {},
    onOpenProfile  : () -> Unit = {},
    viewModel      : HomeViewModel  = hiltViewModel(),
    notesViewModel : NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val notes   by notesViewModel.notes.collectAsState()

    val streak         = uiState.currentStreak
    val checkedInToday = uiState.currentStreak > 0
    val weekDays       = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
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

    // Игры как практики
    val practices = remember {
        listOf(
            Practice("catch_r",    Icons.Outlined.GpsFixed,        "Поймай\n«Р»",    "3 мин", isNew = true, color = Color(0xFF6C63FF)),
            Practice("guess_word", Icons.Outlined.Hearing,         "Угадай\nслово",  "5 мин", color = Color(0xFF00BCD4)),
            Practice("where_r",    Icons.Outlined.TravelExplore,   "Где\n«Р»?",      "4 мин", color = Color(0xFF4CAF50)),
            Practice("word_r",     Icons.Outlined.RecordVoiceOver, "Слова\nна «Р»",  "5 мин", color = Color(0xFFFF9800)),
            Practice("tongue_twist", Icons.Outlined.RecordVoiceOver, "Скоро-\nговорки","5 мин", color = Color(0xFFE91E63)),
            Practice("poems",      Icons.Outlined.AutoStories,     "Стишки\nс «Р»",  "4 мин", color = Color(0xFF9C27B0)),
            Practice("tongue_ex",  Icons.Outlined.Gesture,         "Язык\nгимнастика","6 мин", color = Color(0xFF009688)),
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    Text(
                        text     = "все игры →",
                        fontSize = 12.sp,
                        color    = SoyleAccent
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text     = "Выбирай игру и тренируй букву «Р» каждый день",
                    fontSize = 13.sp,
                    color    = SoyleTextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(14.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding        = PaddingValues(end = 8.dp)
                ) {
                    items(practices) { practice ->
                        PracticeCard(
                            practice = practice,
                            onClick  = { onOpenGame(practice.id) }
                        )
                    }
                }

                // ── Заметки ───────────────────────────────────────────────────
                Spacer(Modifier.height(28.dp))
                NotesSection(
                    notes          = notes,
                    onAddNote      = { notesViewModel.addNote(it) },
                    onDeleteNote   = { notesViewModel.deleteNote(it) }
                )

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

        // ── Ачивки поверх ─────────────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)) {
            AchievementToastHost(
                achievement = uiState.unlockedAchievement,
                onDismiss   = viewModel::dismissAchievement
            )
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
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SoyleSurface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector        = Icons.Outlined.Whatshot,
                    contentDescription = "Серия",
                    tint               = if (streak > 0) SoyleStreak else SoyleTextMuted,
                    modifier           = Modifier.size(14.dp)
                )
                Text(
                    text       = "$streak",
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (streak > 0) SoyleStreak else SoyleTextMuted
                )
            }
        }
        Text(
            text       = greeting,
            fontWeight = FontWeight.Normal,
            fontSize   = 16.sp,
            color      = SoyleTextPrimary
        )
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
                Text(text = "Чек-ин выполнен.", fontSize = 16.sp, color = SoyleTextMuted)
                SoyleTag(text = "Активен")
            }
        } else {
            Text(text = "Начни чек-ин", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = SoyleTextPrimary)
            Text(text = "Как чувствуешь себя сегодня?", fontSize = 14.sp, color = SoyleTextSecondary)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            weekDays.forEachIndexed { index, day ->
                WeekDayDot(day = day, isActive = index in checkedSet, isToday = index == 0)
            }
        }
    }
}

@Composable
private fun WeekDayDot(day: String, isActive: Boolean, isToday: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(when { isActive -> SoyleTextPrimary; isToday -> SoyleSurface2; else -> Color.Transparent })
                .border(1.dp, if (isToday && !isActive) SoyleBorderLight else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) Icon(Icons.Outlined.Whatshot, null, tint = SoyleBg, modifier = Modifier.size(14.dp))
        }
        Text(text = day, fontSize = 10.sp, color = if (isToday) SoyleTextSecondary else SoyleTextMuted)
    }
}

// ── Карточка практики ─────────────────────────────────────────────────────────

@Composable
private fun PracticeCard(practice: Practice, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(practice.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = practice.icon,
                contentDescription = practice.title,
                tint               = practice.color,
                modifier           = Modifier.size(22.dp)
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
                    text       = "НОВОЕ",
                    fontSize   = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color      = SoyleAccentLight,
                    modifier   = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Text(
            text      = practice.title,
            fontSize  = 11.sp,
            color     = SoyleTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
        Text(
            text     = practice.duration,
            fontSize = 10.sp,
            color    = SoyleTextMuted
        )
    }
}

// ── Раздел заметок ────────────────────────────────────────────────────────────

@Composable
private fun NotesSection(
    notes        : List<String>,
    onAddNote    : (String) -> Unit,
    onDeleteNote : (String) -> Unit
) {
    var showDialog   by remember { mutableStateOf(false) }
    var showAll      by remember { mutableStateOf(false) }
    var inputText    by remember { mutableStateOf("") }

    val displayNotes = if (showAll) notes else notes.take(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(18.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "Мои заметки",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                color      = SoyleTextPrimary
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SoyleAccentSoft)
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Add,
                    contentDescription = "Добавить заметку",
                    tint               = SoyleAccent,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoyleSurface2)
                    .clickable { showDialog = true }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.EditNote, null, tint = SoyleTextMuted, modifier = Modifier.size(24.dp))
                    Text("Записать мысль…", fontSize = 13.sp, color = SoyleTextMuted)
                }
            }
        } else {
            displayNotes.forEach { note ->
                NoteItem(text = note, onDelete = { onDeleteNote(note) })
            }

            if (notes.size > 3) {
                TextButton(
                    onClick  = { showAll = !showAll },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text     = if (showAll) "Скрыть" else "Ещё ${notes.size - 3} заметок →",
                        fontSize = 13.sp,
                        color    = SoyleAccent
                    )
                }
            }
        }
    }

    // Диалог добавления заметки
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; inputText = "" },
            containerColor   = SoyleSurface,
            title = {
                Text("Новая заметка", fontWeight = FontWeight.SemiBold, color = SoyleTextPrimary)
            },
            text = {
                OutlinedTextField(
                    value         = inputText,
                    onValueChange = { inputText = it },
                    placeholder   = { Text("Запиши свои мысли…", color = SoyleTextMuted) },
                    maxLines      = 5,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = SoyleAccent,
                        unfocusedBorderColor    = SoyleBorder,
                        focusedTextColor        = SoyleTextPrimary,
                        unfocusedTextColor      = SoyleTextPrimary,
                        focusedContainerColor   = SoyleSurface2,
                        unfocusedContainerColor = SoyleSurface2,
                        cursorColor             = SoyleAccent
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (inputText.isNotBlank()) { onAddNote(inputText); inputText = "" }
                    showDialog = false
                }) { Text("Сохранить", color = SoyleAccent, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; inputText = "" }) {
                    Text("Отмена", color = SoyleTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun NoteItem(text: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SoyleSurface2)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector        = Icons.Outlined.Notes,
            contentDescription = null,
            tint               = SoyleTextMuted,
            modifier           = Modifier.size(16.dp)
        )
        Text(
            text       = text,
            fontSize   = 13.sp,
            color      = SoyleTextSecondary,
            modifier   = Modifier.weight(1f),
            lineHeight = 18.sp
        )
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector        = Icons.Outlined.Close,
                contentDescription = "Удалить",
                tint               = SoyleTextMuted,
                modifier           = Modifier.size(14.dp)
            )
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
            Text(text = "На этой неделе", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = SoyleTextPrimary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem(value = "${checkedDays.size}", label = "занятий")
                StatItem(value = "$streak", label = "серия", showFire = streak > 0)
                StatItem(value = if (avgScore > 0) "$avgScore%" else "—", label = "ср. оценка")
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
                                        .background(if (i < checkedDays.size) SoyleTextPrimary else SoyleSurface)
                                        .border(1.dp, if (i < checkedDays.size) Color.Transparent else SoyleBorderLight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (i < checkedDays.size)
                                        Icon(Icons.Outlined.Check, null, tint = SoyleBg, modifier = Modifier.size(14.dp))
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
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            if (showFire) Icon(Icons.Outlined.Whatshot, null, tint = SoyleStreak, modifier = Modifier.size(16.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = if (showFire) SoyleStreak else SoyleTextPrimary)
        }
        Text(label, fontSize = 11.sp, color = SoyleTextSecondary)
    }
}
