package com.example.soyle.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Модели ────────────────────────────────────────────────────────────────────

enum class GameCategory { PHONEME, BREATHING, VOCABULARY, TONGUE }

data class GameItem(
    val id          : String,
    val icon        : String,
    val title       : String,
    val description : String,
    val category    : GameCategory,
    val difficulty  : Int,          // 1-3
    val duration    : String,
    val isFeatured  : Boolean = false,
    val isLocked    : Boolean = false
)

// ── Экран игр ─────────────────────────────────────────────────────────────────

@Composable
fun GamesScreen(
    onOpenGame: (String) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<GameCategory?>(null) }

    val allGames = remember {
        listOf(
            GameItem("catch_r",  "🎯", "Поймай букву «Р»",    "Нажимай только на нужную букву",         GameCategory.PHONEME,    1, "3 мин", isFeatured = true),
            GameItem("breath1",  "🌬️", "Воздушный шарик",     "Надуй шарик правильным дыханием",        GameCategory.BREATHING,  1, "2 мин"),
            GameItem("word_r",   "🔤", "Слова на «Р»",         "Произнеси слово — получи очко",          GameCategory.PHONEME,    2, "5 мин"),
            GameItem("tongue1",  "👅", "Гимнастика языка",     "Следуй за упражнениями",                 GameCategory.TONGUE,     1, "4 мин"),
            GameItem("echo",     "🎵", "Эхо",                  "Повтори звук точь-в-точь",               GameCategory.PHONEME,    2, "5 мин"),
            GameItem("story",    "📖", "Составь историю",      "Придумай рассказ по картинке",           GameCategory.VOCABULARY, 3, "8 мин"),
            GameItem("whisper",  "🤫", "Шёпот и крик",         "Управляй громкостью голоса",             GameCategory.BREATHING,  1, "3 мин"),
            GameItem("tongue2",  "🐍", "Змейка-язычок",        "Двигай языком по дорожке",               GameCategory.TONGUE,     2, "5 мин"),
            GameItem("syllable", "🎶", "Ритм слогов",          "Воспроизведи ритмический паттерн",       GameCategory.PHONEME,    2, "4 мин"),
            GameItem("vocab",    "🌟", "Слово дня",            "Изучи новое слово",                      GameCategory.VOCABULARY, 1, "2 мин"),
            GameItem("hard1",    "🔬", "Скороговорки",         "Скажи как можно быстрее и чисто",        GameCategory.PHONEME,    3, "6 мин", isLocked = true),
        )
    }

    val filtered = if (selectedCategory == null) allGames
    else allGames.filter { it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        // ── Заголовок ─────────────────────────────────────────────────────
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
            // Кнопка поиска
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoyleSurface),
                contentAlignment = Alignment.Center
            ) {
                Text("🔍", fontSize = 16.sp)
            }
        }

        // ── Апрельский баннер (как Featured в Stoic Explore) ──────────────
        FeaturedBanner(game = allGames.first { it.isFeatured }, onClick = onOpenGame)

        // ── Фильтры категорий ──────────────────────────────────────────────
        Spacer(Modifier.height(20.dp))
        CategoryFilterRow(
            selected  = selectedCategory,
            onSelect  = { selectedCategory = if (selectedCategory == it) null else it }
        )

        // ── Список игр ────────────────────────────────────────────────────
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier            = Modifier.weight(1f),
            contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Парами (2 в строке — как в Stoic Explore)
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
                    // Заглушка если нечётное
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ── Баннер фичи ──────────────────────────────────────────────────────────────

@Composable
private fun FeaturedBanner(game: GameItem, onClick: (String) -> Unit) {
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
            // Иллюстрация
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .background(SoyleAccentSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(game.icon, fontSize = 48.sp)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text     = "РЕКОМЕНДУЕМ",
                    fontSize = 9.sp,
                    color    = SoyleTextMuted,
                    fontWeight = FontWeight.SemiBold,
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
                    text     = game.description,
                    fontSize = 12.sp,
                    color    = SoyleTextSecondary,
                    lineHeight = 17.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SoyleButtonPrimary)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text     = "Играть →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color    = SoyleButtonPrimaryText
                    )
                }
            }
        }
    }
}

// ── Фильтры категорий ─────────────────────────────────────────────────────────

@Composable
private fun CategoryFilterRow(
    selected : GameCategory?,
    onSelect : (GameCategory) -> Unit
) {
    val categories = listOf(
        GameCategory.PHONEME    to "Звуки",
        GameCategory.BREATHING  to "Дыхание",
        GameCategory.TONGUE     to "Язык",
        GameCategory.VOCABULARY to "Слова"
    )
    Row(
        modifier           = Modifier
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
                    text     = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color    = if (isActive) SoyleButtonPrimaryText else SoyleTextSecondary
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
                    .background(if (game.isLocked) SoyleSurface2 else SoyleSurface2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = game.icon,
                    fontSize = 40.sp,
                    color = if (game.isLocked) Color(0xFF333333) else Color.Unspecified
                )
                if (game.isLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoyleAccentSoft)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("🔒", fontSize = 9.sp)
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Сложность
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