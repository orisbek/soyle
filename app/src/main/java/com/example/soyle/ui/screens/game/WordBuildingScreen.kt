package com.example.soyle.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.theme.*
import kotlinx.coroutines.delay

// ── 50 уровней (слова с буквой Р) ────────────────────────────────────────────

data class WordLevel(
    val number : Int,
    val word   : String,
    val hint   : String
)

val ALL_WORD_LEVELS = listOf(
    // Лёгкий (1–15)
    WordLevel(1,  "РАК",     "🦀 Живёт в воде"),
    WordLevel(2,  "РОТ",     "😮 Часть лица"),
    WordLevel(3,  "РЯД",     "📏 Один за другим"),
    WordLevel(4,  "РОГ",     "🦌 Растёт на голове"),
    WordLevel(5,  "РАЙ",     "☁️ Прекрасное место"),
    WordLevel(6,  "РЫБА",    "🐟 Плавает в реке"),
    WordLevel(7,  "РУКА",    "✋ Часть тела"),
    WordLevel(8,  "РОЗА",    "🌹 Красивый цветок"),
    WordLevel(9,  "РАМА",    "🖼 Держит картину"),
    WordLevel(10, "РОСТ",    "📏 Высота человека"),
    WordLevel(11, "РЫСЬ",    "🐆 Дикая кошка"),
    WordLevel(12, "РОМБ",    "💎 Фигура из 4 сторон"),
    WordLevel(13, "РУЛЬ",    "🚗 Управляет машиной"),
    WordLevel(14, "РОЖЬ",    "🌾 Злак в поле"),
    WordLevel(15, "ГОРА",    "⛰ Высокая вершина"),
    // Средний (16–30)
    WordLevel(16, "НОРА",    "🐇 Домик зверя"),
    WordLevel(17, "КОРА",    "🌳 Одежда дерева"),
    WordLevel(18, "ПАРА",    "2️⃣ Два предмета"),
    WordLevel(19, "ВЕРА",    "🙏 Доверие"),
    WordLevel(20, "ТРАВА",   "🌿 Зелёный ковёр"),
    WordLevel(21, "РУКАВ",   "👕 Часть одежды"),
    WordLevel(22, "РЫНОК",   "🛒 Место торговли"),
    WordLevel(23, "РЕЛЬС",   "🚂 Железная дорога"),
    WordLevel(24, "РУБЛЬ",   "💰 Деньги"),
    WordLevel(25, "РЫБАК",   "🎣 Ловит рыбу"),
    WordLevel(26, "ГОРОД",   "🏙 Много домов"),
    WordLevel(27, "РЫЖИК",   "🍄 Гриб"),
    WordLevel(28, "РУПОР",   "📢 Усиливает голос"),
    WordLevel(29, "РЫЦАРЬ",  "⚔️ Воин в броне"),
    WordLevel(30, "ДОРОГА",  "🛣 По ней едут машины"),
    // Сложный (31–50)
    WordLevel(31, "РАКЕТА",  "🚀 Летит в космос"),
    WordLevel(32, "РАДУГА",  "🌈 После дождя"),
    WordLevel(33, "РАБОТА",  "💼 Чем занимаются взрослые"),
    WordLevel(34, "РЕКОРД",  "🏆 Лучший результат"),
    WordLevel(35, "ПРИРОДА", "🌲 Лес, поля, реки"),
    WordLevel(36, "ПРИВЕТ",  "👋 Приветствие"),
    WordLevel(37, "ПРЫЖОК",  "🏃 Прыгнуть высоко"),
    WordLevel(38, "ПРИМЕР",  "📝 Задача по математике"),
    WordLevel(39, "РОССИЯ",  "🇷🇺 Наша страна"),
    WordLevel(40, "РИСУНОК", "🎨 Нарисованный образ"),
    WordLevel(41, "РАКУШКА", "🐚 Домик моллюска"),
    WordLevel(42, "РАДОСТЬ", "😄 Хорошее настроение"),
    WordLevel(43, "РЕБЁНОК", "👶 Маленький человек"),
    WordLevel(44, "КРАСОТА", "💐 Что-то прекрасное"),
    WordLevel(45, "РАССВЕТ", "🌅 Когда встаёт солнце"),
    WordLevel(46, "ПРОДУКТ", "🥗 Еда на столе"),
    WordLevel(47, "ПРОСЬБА", "🙏 Вежливая просьба"),
    WordLevel(48, "РАССКАЗ", "📖 Маленькая история"),
    WordLevel(49, "РАСТЕНИЕ","🌱 Дерево или цветок"),
    WordLevel(50, "КРОКОДИЛ","🐊 Зелёный хищник")
)

// ── Экран выбора уровня (6 случайных из 50) ──────────────────────────────────

@Composable
fun LevelSelectScreen(
    onLevelSelected : (WordLevel) -> Unit,
    onBack          : () -> Unit
) {
    val randomLevels = remember { ALL_WORD_LEVELS.shuffled().take(6) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidsBg)
    ) {
        // Шапка
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(KidsOrange, KidsPink)))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text       = "🔤 Собери слово!",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Black,
                        color      = Color.White
                    )
                    Text(
                        text       = "Выбери уровень",
                        fontSize   = 13.sp,
                        color      = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Объяснение
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(KidsMintLight, KidsBlueLight)))
                    .border(3.dp, KidsMint, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🦜", fontSize = 36.sp)
                Column {
                    Text(
                        text       = "Собери слово из букв!",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = KidsTextPrimary
                    )
                    Text(
                        text       = "Нажимай на буквы по порядку",
                        fontSize   = 13.sp,
                        color      = KidsTextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text       = "🎲 Твои 6 уровней:",
                fontSize   = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = KidsTextPrimary
            )

            randomLevels.forEach { level ->
                LevelCard(level = level, onClick = { onLevelSelected(level) })
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

private val CARD_GRADIENTS = listOf(
    listOf(KidsMintLight, Color(0xFFD0F8F5)),
    listOf(KidsBlueLight, Color(0xFFCCE5FF)),
    listOf(KidsPinkLight, Color(0xFFFFD6E8)),
    listOf(KidsYellowLight, Color(0xFFFFF0B0)),
    listOf(KidsGreenLight, Color(0xFFB8F0C0)),
    listOf(Color(0xFFEDE7FF), Color(0xFFD8CFFF))
)
private val CARD_BORDERS = listOf(KidsMint, KidsBlue, KidsPink, KidsYellow, KidsGreen, KidsPurple)

@Composable
private fun LevelCard(level: WordLevel, onClick: () -> Unit) {
    val idx = (level.number - 1) % CARD_GRADIENTS.size

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(CARD_GRADIENTS[idx]))
            .border(3.dp, CARD_BORDERS[idx], RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(CARD_BORDERS[idx]),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "${level.number}",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Black,
                color      = Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            // Пустые слоты — намёк на длину слова
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(level.word.length) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.8f))
                            .border(1.dp, CARD_BORDERS[idx].copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text       = level.hint,
                fontSize   = 12.sp,
                color      = KidsTextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text("▶", fontSize = 20.sp, color = CARD_BORDERS[idx], fontWeight = FontWeight.Black)
    }
}

// ── Экран игры «Собери слово» ─────────────────────────────────────────────────

@Composable
fun WordBuildingScreen(
    level   : WordLevel,
    onBack  : () -> Unit,
    onFinish: (score: Int) -> Unit
) {
    val word = level.word
    // Перемешанные буквы — храним как indexed пары для корректного отслеживания
    val indexedLetters = remember {
        word.toList().mapIndexed { i, ch -> i to ch }.shuffled()
    }

    var assembledIndices by remember { mutableStateOf(listOf<Int>()) }
    var gameResult       by remember { mutableStateOf<Boolean?>(null) }
    var showCelebration  by remember { mutableStateOf(false) }
    var shakeWrong       by remember { mutableStateOf(false) }

    val assembled = assembledIndices.map { idx -> indexedLetters[idx].second }

    LaunchedEffect(assembledIndices.size) {
        if (assembledIndices.size == word.length) {
            val correct = assembled.joinToString("") == word
            gameResult = correct
            if (correct) {
                showCelebration = true
                delay(2000)
                onFinish(100)
            } else {
                shakeWrong = true
                delay(600)
                shakeWrong = false
                assembledIndices = listOf()
                gameResult = null
            }
        }
    }

    val shakeOffsetX by animateFloatAsState(
        targetValue   = if (shakeWrong) 12f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh),
        label         = "shake"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KidsBg)
        ) {
            // Шапка
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(KidsOrange, KidsPink)))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("←", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text       = "Уровень ${level.number}",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Black,
                            color      = Color.White
                        )
                        Text(
                            text       = level.hint,
                            fontSize   = 13.sp,
                            color      = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Маскот
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(KidsMintLight, KidsBlueLight)))
                        .border(3.dp, KidsMint.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🦜", fontSize = 38.sp)
                    Text(
                        text       = when (gameResult) {
                            true  -> "Верно! Молодец! 🎉"
                            false -> "Упс! Попробуй снова! 💪"
                            null  -> "Нажимай на буквы по порядку, чтобы собрать слово!"
                        },
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = KidsTextPrimary,
                        lineHeight = 20.sp,
                        modifier   = Modifier.weight(1f)
                    )
                }

                // Слоты для собранного слова
                Row(
                    modifier              = Modifier.offset(x = shakeOffsetX.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    word.forEachIndexed { idx, _ ->
                        val ch = assembled.getOrNull(idx)
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (ch != null)
                                        Brush.radialGradient(listOf(KidsMintLight, Color(0xFFD0FAF7)))
                                    else
                                        Brush.radialGradient(listOf(Color(0xFFF0F0F0), Color(0xFFE0E0E0)))
                                )
                                .border(
                                    3.dp,
                                    if (ch != null) KidsMint else KidsBorder,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = ch?.toString() ?: "",
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Black,
                                color      = if (ch != null) KidsMintDark else KidsTextDisabled
                            )
                        }
                    }
                }

                // Прогресс
                Text(
                    text       = "${assembled.size} / ${word.length} букв",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = KidsTextSecondary
                )

                // Разделитель
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(KidsBorder)
                )

                // Перемешанные буквы
                Text(
                    text       = "Буквы:",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = KidsTextPrimary,
                    modifier   = Modifier.fillMaxWidth()
                )

                // Сетка перемешанных букв (5 в ряд)
                val rows = indexedLetters.chunked(5)
                var globalTileIdx = 0
                rows.forEach { rowLetters ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        rowLetters.forEach { (_, letter) ->
                            val tileIdx = globalTileIdx++
                            val isUsed  = tileIdx in assembledIndices
                            LetterTile(
                                letter  = letter,
                                isUsed  = isUsed,
                                onClick = {
                                    if (!isUsed && gameResult == null) {
                                        assembledIndices = assembledIndices + tileIdx
                                    }
                                }
                            )
                        }
                    }
                }

                // Кнопка сброса
                if (assembledIndices.isNotEmpty() && gameResult == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(KidsPinkLight)
                            .border(2.dp, KidsPink, RoundedCornerShape(16.dp))
                            .clickable {
                                assembledIndices = listOf()
                                gameResult       = null
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "🔄 Сбросить",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = KidsPink
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }

        // Оверлей победы
        AnimatedVisibility(
            visible = showCelebration,
            enter   = scaleIn(initialScale = 0.6f) + fadeIn(),
            exit    = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.radialGradient(listOf(KidsYellowLight, KidsMintLight)))
                    .border(4.dp, KidsYellow, RoundedCornerShape(28.dp))
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🎉🏆🎉", fontSize = 44.sp)
                    Text(
                        text       = "«$word» — собрано!",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Black,
                        color      = KidsTextPrimary,
                        textAlign  = TextAlign.Center
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        repeat(3) { Text("⭐", fontSize = 28.sp) }
                    }
                    Text(
                        text       = "+100 XP",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = KidsYellowDark
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterTile(
    letter  : Char,
    isUsed  : Boolean,
    onClick : () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue   = if (isUsed) 0.8f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "tileScale"
    )

    Box(
        modifier = Modifier
            .padding(4.dp)
            .scale(scale)
            .size(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isUsed)
                    Brush.radialGradient(listOf(Color(0xFFDDDDDD), Color(0xFFCCCCCC)))
                else
                    Brush.radialGradient(listOf(KidsOrange, Color(0xFFD06010)))
            )
            .border(
                3.dp,
                if (isUsed) Color.Transparent else KidsYellow.copy(alpha = 0.8f),
                RoundedCornerShape(14.dp)
            )
            .clickable(enabled = !isUsed, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = if (isUsed) "" else letter.toString(),
            fontSize   = 26.sp,
            fontWeight = FontWeight.Black,
            color      = Color.White,
            textAlign  = TextAlign.Center
        )
    }
}
