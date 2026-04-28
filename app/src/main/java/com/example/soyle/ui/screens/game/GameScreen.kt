package com.example.soyle.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Модели ────────────────────────────────────────────────────────────────────

enum class GameCategory { PHONEME, VOCABULARY, TONGUE }

data class GameItem(
    val id          : String,
    val icon        : ImageVector,
    val title       : String,
    val description : String,
    val category    : GameCategory,
    val difficulty  : Int,          // 1-3
    val duration    : String,
    val isFeatured  : Boolean = false,
    val isLocked    : Boolean = false,
    val isNew       : Boolean = false,
    val tags        : List<String> = emptyList()
)

// ── Список всех игр ────────────────────────────────────────────────────────────

private val ALL_GAMES = listOf(
    GameItem("catch_r",     Icons.Outlined.GpsFixed,        "Поймай букву «Р»",  "Нажимай только на нужную букву",     GameCategory.PHONEME,    1, "3 мин", isFeatured = true, tags = listOf("р", "буква", "фонема")),
    GameItem("guess_word",  Icons.Outlined.Hearing,         "Угадай слово",      "Послушай и выбери правильный ответ", GameCategory.PHONEME,    2, "5 мин",                   tags = listOf("р", "слова", "слушать", "угадай")),
    GameItem("where_r",     Icons.Outlined.TravelExplore,   "Где буква «Р»?",    "Найди позицию буквы в слове",        GameCategory.PHONEME,    2, "4 мин",                   tags = listOf("р", "позиция", "начало", "конец")),
    GameItem("word_r",      Icons.Outlined.RecordVoiceOver, "Слова на «Р»",      "Произнеси слово — получи очко",      GameCategory.PHONEME,    3, "5 мин",                   tags = listOf("р", "слова", "произношение")),
    GameItem("tongue_twist",Icons.Outlined.Speed,           "Скороговорки",      "Говори быстро и чётко",              GameCategory.PHONEME,    2, "5 мин", isNew = true,      tags = listOf("скороговорка", "р", "быстро")),
    GameItem("poems",       Icons.Outlined.AutoStories,     "Стишки с «Р»",      "Читай стихи — тренируй звук",        GameCategory.VOCABULARY, 1, "4 мин", isNew = true,      tags = listOf("стих", "р", "читать", "строчки")),
    GameItem("tongue_ex",   Icons.Outlined.Gesture,         "Гимнастика языка",  "Упражнения для правильного звука",   GameCategory.TONGUE,     1, "6 мин", isNew = true,      tags = listOf("язык", "гимнастика", "упражнение")),
)

/** Маппинг цели пользователя → id рекомендуемой игры */
private fun featuredIdForGoal(goal: String): String = when {
    goal.contains("угадай", ignoreCase = true) || goal.contains("слух", ignoreCase = true) -> "guess_word"
    goal.contains("где", ignoreCase = true)  || goal.contains("позиц", ignoreCase = true)  -> "where_r"
    goal.contains("слова", ignoreCase = true) || goal.contains("произнос", ignoreCase = true) -> "word_r"
    else                                                                                    -> "catch_r"
}

// ── Экран игр ─────────────────────────────────────────────────────────────────

@Composable
fun GamesScreen(
    onOpenGame : (String) -> Unit = {},
    viewModel  : GamesViewModel   = hiltViewModel()
) {
    val userGoal    by viewModel.userGoal.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var searchActive     by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<GameCategory?>(null) }
    val focusRequester   = remember { FocusRequester() }

    // Рекомендуемая игра на основе цели
    val featuredId   = featuredIdForGoal(userGoal)
    val featuredGame = ALL_GAMES.first { it.id == featuredId }

    // Фильтрация по поиску + категории
    val filtered = remember(searchQuery, selectedCategory) {
        ALL_GAMES.filter { game ->
            val matchesSearch = searchQuery.isBlank() ||
                game.title.contains(searchQuery, ignoreCase = true) ||
                game.description.contains(searchQuery, ignoreCase = true) ||
                game.tags.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesCategory = selectedCategory == null || game.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        // ── Заголовок + поиск ──────────────────────────────────────────────
        AnimatedContent(
            targetState   = searchActive,
            transitionSpec = {
                fadeIn(tween(150)) togetherWith fadeOut(tween(150))
            },
            label = "searchToggle"
        ) { isSearching ->
            if (isSearching) {
                // Строка поиска
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value         = searchQuery,
                        onValueChange = viewModel::updateSearch,
                        placeholder   = {
                            Text("Поиск игр...", color = SoyleTextMuted, fontSize = 14.sp)
                        },
                        singleLine    = true,
                        leadingIcon   = {
                            Icon(
                                imageVector        = Icons.Outlined.Search,
                                contentDescription = null,
                                tint               = SoyleTextMuted,
                                modifier           = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon  = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearch("") }) {
                                    Icon(
                                        imageVector        = Icons.Outlined.Close,
                                        contentDescription = "Очистить",
                                        tint               = SoyleTextMuted,
                                        modifier           = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier      = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        shape         = RoundedCornerShape(14.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor   = SoyleSurface,
                            unfocusedContainerColor = SoyleSurface,
                            focusedBorderColor      = SoyleAccent,
                            unfocusedBorderColor    = SoyleBorder,
                            focusedTextColor        = SoyleTextPrimary,
                            unfocusedTextColor      = SoyleTextPrimary,
                            cursorColor             = SoyleAccent
                        )
                    )
                    TextButton(onClick = {
                        searchActive = false
                        viewModel.updateSearch("")
                    }) {
                        Text("Отмена", color = SoyleTextSecondary, fontSize = 14.sp)
                    }

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
            } else {
                // Обычная шапка
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text          = "игры.",
                        fontWeight    = FontWeight.Bold,
                        fontSize      = 34.sp,
                        color         = SoyleTextPrimary,
                        letterSpacing = (-1).sp
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SoyleSurface)
                            .clickable { searchActive = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Outlined.Search,
                            contentDescription = "Поиск",
                            tint               = SoyleTextSecondary,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ── Рекомендуемый баннер (скрывается при поиске) ──────────────────
        if (searchQuery.isBlank()) {
            FeaturedBanner(
                game    = featuredGame,
                goal    = userGoal,
                onClick = onOpenGame
            )
            Spacer(Modifier.height(16.dp))

            // Фильтры категорий
            CategoryFilterRow(
                selected = selectedCategory,
                onSelect = { selectedCategory = if (selectedCategory == it) null else it }
            )
            Spacer(Modifier.height(16.dp))
        }

        // ── Список игр ────────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        tint               = SoyleTextMuted,
                        modifier           = Modifier.size(40.dp)
                    )
                    Text("Ничего не найдено", fontSize = 15.sp, color = SoyleTextMuted)
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.weight(1f),
                contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val pairs = filtered.chunked(2)
                items(pairs) { pair ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { game ->
                            GameCard(
                                game     = game,
                                onClick  = { onOpenGame(game.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// ── Баннер фичи ──────────────────────────────────────────────────────────────

@Composable
private fun FeaturedBanner(game: GameItem, goal: String, onClick: (String) -> Unit) {
    val subtitle = if (goal.isNotBlank()) "На основе вашей цели" else "РЕКОМЕНДУЕМ"
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(18.dp))
            .clickable { onClick(game.id) }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .background(SoyleAccentSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = game.icon,
                    contentDescription = game.title,
                    tint               = SoyleAccent,
                    modifier           = Modifier.size(52.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text          = subtitle.uppercase(),
                    fontSize      = 9.sp,
                    color         = SoyleTextMuted,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text       = game.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = SoyleTextPrimary,
                    lineHeight = 22.sp
                )
                Text(
                    text       = game.description,
                    fontSize   = 12.sp,
                    color      = SoyleTextSecondary,
                    lineHeight = 17.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SoyleButtonPrimary)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text       = "Играть →",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = SoyleButtonPrimaryText
                    )
                }
            }
        }
    }
}

// ── Фильтры категорий ─────────────────────────────────────────────────────────

@Composable
private fun CategoryFilterRow(selected: GameCategory?, onSelect: (GameCategory) -> Unit) {
    val categories = listOf(
        GameCategory.PHONEME    to "🎯 Звук «Р»",
        GameCategory.VOCABULARY to "📖 Стишки",
        GameCategory.TONGUE     to "👅 Язык",
    )
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (cat, label) ->
            val isActive = selected == cat
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isActive) SoyleButtonPrimary else SoyleSurface)
                    .border(1.dp, if (isActive) Color.Transparent else SoyleBorder, RoundedCornerShape(20.dp))
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text       = label,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color      = if (isActive) SoyleButtonPrimaryText else SoyleTextSecondary
                )
            }
        }
    }
}

// ── Карточка игры ─────────────────────────────────────────────────────────────

@Composable
private fun GameCard(game: GameItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
            .clickable(enabled = !game.isLocked, onClick = onClick)
    ) {
        Column {
            // Верх с иконкой
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(SoyleSurface2),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = game.icon,
                    contentDescription = game.title,
                    tint               = if (game.isLocked) SoyleTextMuted else SoyleAccent,
                    modifier           = Modifier.size(40.dp)
                )
                when {
                    game.isLocked -> Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoyleSurface)
                            .border(1.dp, SoyleBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(Icons.Outlined.Lock, "Заблокировано", tint = SoyleTextMuted, modifier = Modifier.size(10.dp))
                    }
                    game.isNew -> Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoyleAccent)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("NEW", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SoyleButtonPrimaryText)
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Сложность (точки)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(3) { i ->
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (i < game.difficulty) SoyleAccent else SoyleSurface3)
                        )
                    }
                }
                Text(
                    text       = game.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    color      = if (game.isLocked) SoyleTextMuted else SoyleTextPrimary,
                    lineHeight = 18.sp
                )
                Text(
                    text     = game.duration,
                    fontSize = 11.sp,
                    color    = SoyleTextMuted
                )
            }
        }
    }
}
