package com.example.soyle.ui.screens.game

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.ui.screens.exercise.ExerciseUiState
import com.example.soyle.ui.screens.exercise.ExerciseViewModel
import com.example.soyle.ui.theme.*
import java.util.Locale
import kotlinx.coroutines.delay

data class WordLevel(val number: Int, val word: String, val hint: String)

val ALL_WORD_LEVELS = listOf(
    WordLevel(1, "РАК", "В воде живет"),
    WordLevel(2, "РЫБА", "Плавает в реке"),
    WordLevel(3, "РУКА", "Часть тела"),
    WordLevel(4, "РОТ", "Говорит и ест"),
    WordLevel(5, "РОГ", "На голове у оленя"),
    WordLevel(6, "РИС", "Белая крупа"),
    WordLevel(7, "РЕКА", "Течет вода"),
    WordLevel(8, "РОМ", "Напиток пирата"),
    WordLevel(9, "РЯД", "Один за другим"),
    WordLevel(10, "РОСТ", "Человек высокий"),
    WordLevel(11, "РОБОТ", "Железный помощник"),
    WordLevel(12, "РАМА", "Для окна или картины"),
    WordLevel(13, "РАНА", "Больное место"),
    WordLevel(14, "РАДУГА", "Цветной мостик в небе"),
    WordLevel(15, "РАБОТА", "Взрослые туда ходят"),
    WordLevel(16, "РУЧКА", "Ей пишут в тетради"),
    WordLevel(17, "РЫНОК", "Где покупают овощи"),
    WordLevel(18, "РОЗА", "Колючий цветок"),
    WordLevel(19, "РУБАШКА", "Одежда с пуговицами"),
    WordLevel(20, "РЕМЕНЬ", "Держит штаны"),
    WordLevel(21, "РАДИО", "Передает музыку"),
    WordLevel(22, "РЮКЗАК", "Носят за спиной"),
    WordLevel(23, "РИСУНОК", "Творчество на бумаге"),
    WordLevel(24, "РЕШЕНИЕ", "Ответ на задачу"),
    WordLevel(25, "РЕЖИМ", "Распорядок дня"),
    WordLevel(26, "РЕЦЕПТ", "Как готовить еду"),
    WordLevel(27, "РЕЗУЛЬТАТ", "Итог стараний"),
    WordLevel(28, "РЕАЛЬНОСТЬ", "Мир вокруг нас"),
    WordLevel(29, "РАЗВИТИЕ", "Рост и обучение"),
    WordLevel(30, "РАЗГОВОР", "Общение людей"),
    WordLevel(31, "РЫЦАРЬ", "Герой в доспехах"),
    WordLevel(32, "РАКЕТА", "Летит к звездам"),
    WordLevel(33, "РАКУШКА", "Домик улитки"),
    WordLevel(34, "РЕЗИНКА", "Стирает карандаш"),
    WordLevel(35, "РЕДИС", "Красный овощ"),
    WordLevel(36, "РЕДКИЙ", "Необычный предмет"),
    WordLevel(37, "РЕЗАТЬ", "Работа ножницами"),
    WordLevel(38, "РАСТИ", "Становиться больше"),
    WordLevel(39, "РИСОВАТЬ", "Создавать красоту"),
    WordLevel(40, "РЫЧАТЬ", "Как лев"),
    WordLevel(41, "РЫЖИЙ", "Цвет лисицы"),
    WordLevel(42, "РЕЗВЫЙ", "Быстрый бег"),
    WordLevel(43, "РАДОСТНЫЙ", "Веселый человек"),
    WordLevel(44, "РАННИЙ", "На рассвете"),
    WordLevel(45, "РЕЗКИЙ", "Быстрый и сильный"),
    WordLevel(46, "РОВНЫЙ", "Гладкий путь"),
    WordLevel(47, "РОБКИЙ", "Стеснительный"),
    WordLevel(48, "РАЗУМНЫЙ", "Очень умный"),
    WordLevel(49, "РОДНОЙ", "Близкий человек"),
    WordLevel(50, "РЕЧНОЙ", "Относится к реке"),
    WordLevel(51, "РЕБРО", "Часть скелета"),
    WordLevel(52, "РОМАШКА", "Белый цветок"),
    WordLevel(53, "РУЧЕЙ", "Маленькая речка"),
    WordLevel(54, "РЫБОЛОВ", "Кто ловит рыбу"),
    WordLevel(55, "РАССВЕТ", "Утро начинается"),
    WordLevel(56, "РАССКАЗ", "История в книге"),
    WordLevel(57, "РАМКА", "Вокруг фото"),
    WordLevel(58, "АРБУЗ", "Сладкая ягода"),
    WordLevel(59, "РОСТОК", "Маленький цветок"),
    WordLevel(60, "РЫСЬ", "Дикая кошка"),
    WordLevel(61, "РАБОТАЮЩИЙ", "Кто сейчас трудится"),
    WordLevel(62, "РАСТУЩИЙ", "Становится выше"),
    WordLevel(63, "РИСУЮЩИЙ", "Кто держит кисть"),
    WordLevel(64, "РЕШАЮЩИЙ", "Важный момент"),
    WordLevel(65, "РАЗБИТЫЙ", "Сломанная вещь"),
    WordLevel(66, "РАСКРЫТЫЙ", "Открытая книга"),
    WordLevel(67, "РАЗНЫЙ", "Не похожий на другой"),
    WordLevel(68, "РОВЕСНИК", "Одинакового возраста"),
    WordLevel(69, "РОДИТЕЛЬ", "Папа или мама"),
    WordLevel(70, "РЕБЁНОК", "Маленький человек"),
    WordLevel(71, "РОЩА", "Маленький лес"),
    WordLevel(72, "РЕФЛЕКС", "Быстрая реакция"),
    WordLevel(73, "РЕГИСТР", "Список имен"),
    WordLevel(74, "РЕМОНТ", "Чиним квартиру"),
    WordLevel(75, "РЕГИОН", "Часть страны"),
    WordLevel(76, "РЕСУРС", "Запас сил"),
    WordLevel(77, "РЕЙТИНГ", "Список лучших"),
    WordLevel(78, "РЕЦЕПТ", "Как варить суп"),
    WordLevel(79, "РЕАКЦИЯ", "Ответ на действие"),
    WordLevel(80, "РЕКОРД", "Самый лучший"),
    WordLevel(81, "РАССКАЗЧИК", "Кто говорит историю"),
    WordLevel(82, "РАБОТНИК", "Кто трудится"),
    WordLevel(83, "РУКОВОДИТЕЛЬ", "Главный начальник"),
    WordLevel(84, "РЕДАКТОР", "Исправляет тексты"),
    WordLevel(85, "РАЗВЕДЧИК", "Ищет секреты"),
    WordLevel(86, "РЫБАК", "Сидит с удочкой"),
    WordLevel(87, "РАКЕТЧИК", "Кто пускает ракеты"),
    WordLevel(88, "РЕСТАВРАТОР", "Чинит старые вещи"),
    WordLevel(89, "РЕЖИССЁР", "Снимает кино"),
    WordLevel(90, "РЕНТГЕН", "Видит косточки"),
    WordLevel(91, "РИСОВАНИЕ", "Урок творчества"),
    WordLevel(92, "РАЗВИТИЕ", "Учеба и рост"),
    WordLevel(93, "РАЗМИНКА", "Зарядка утром"),
    WordLevel(94, "РАЗГОВОРЧИК", "Короткая беседа"),
    WordLevel(95, "РАДОСТЬ", "Счастье в душе"),
    WordLevel(96, "РОЖДЕНИЕ", "День праздника"),
    WordLevel(97, "РЕШЕНИЕ", "Правильный ответ"),
    WordLevel(98, "РАЗРУШЕНИЕ", "Всё сломалось"),
    WordLevel(99, "РАВНОВЕСИЕ", "Не падать"),
    WordLevel(100, "РАЗНООБРАЗИЕ", "Много всего разного")
)

@Composable
fun LevelSelectScreen(
    unlockedLevels: List<Int> = listOf(1), // TODO: Брать из ViewModel
    onLevelSelected: (WordLevel) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    
    DisposableEffect(context) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale("ru")
        }
        tts = ttsInstance
        onDispose { ttsInstance.stop(); ttsInstance.shutdown() }
    }

    val speak = { text: String -> tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) }

    Column(modifier = Modifier.fillMaxSize().background(KidsBg)) {
        Box(
            modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(KidsOrange, KidsPink))).padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Text("←", fontSize = 24.sp, color = Color.White) }
                Text("🔤 Карта уровней", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(KidsMintLight).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🦜", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("Проходи уровни по порядку!", fontWeight = FontWeight.Bold, color = KidsTextPrimary)
                }
            }

            items(ALL_WORD_LEVELS) { level ->
                val isUnlocked = level.number <= (unlockedLevels.maxOrNull() ?: 1)
                LevelBubble(
                    level = level, 
                    isUnlocked = isUnlocked,
                    onClick = { 
                        if (isUnlocked) {
                            speak(level.word)
                            onLevelSelected(level) 
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelBubble(level: WordLevel, isUnlocked: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked) Brush.radialGradient(listOf(KidsMint, KidsMintDark))
                    else Brush.radialGradient(listOf(Color.Gray, Color.DarkGray))
                )
                .border(4.dp, if (isUnlocked) Color.White else Color.LightGray, CircleShape)
                .clickable(enabled = isUnlocked) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Text("${level.number}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            } else {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
            }
        }
        Text(
            text = if (isUnlocked) level.word else "???",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUnlocked) KidsTextPrimary else KidsTextDisabled
        )
    }
}

@Composable
fun WordBuildingScreen(
    level: WordLevel,
    onBack: () -> Unit,
    onNextLevel: () -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var mascotText by remember { mutableStateOf("Собери слово «${level.word}»") }

    DisposableEffect(context) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale("ru")
        }
        tts = ttsInstance
        onDispose { ttsInstance.stop(); ttsInstance.shutdown() }
    }

    val speak = { text: String -> tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) }

    val word = level.word
    val indexedLetters = remember(level) { word.toList().mapIndexed { i, ch -> i to ch }.shuffled() }
    var assembledIndices by remember { mutableStateOf(listOf<Int>()) }
    var isWordAssembled by remember { mutableStateOf(false) }
    var shakeWrong by remember { mutableStateOf(false) }

    val assembled = assembledIndices.map { idx -> indexedLetters[idx].second }

    // Текст маскота в зависимости от состояния
    LaunchedEffect(uiState, isWordAssembled, shakeWrong) {
        mascotText = when {
            shakeWrong -> "Хмм, такое слово точно есть? 🤔"
            uiState is ExerciseUiState.Success -> {
                val score = (uiState as ExerciseUiState.Success).score
                when {
                    score >= 100 -> "Потрясающе! Идеально! 🎉"
                    score == 50 -> (uiState as ExerciseUiState.Success).feedback
                    else -> "Неверно! Попробуй еще раз! 💪"
                }
            }
            isWordAssembled -> "Слово собрано! Теперь скажи «$word» в микрофон!"
            else -> "Собери слово «$word»"
        }
    }

    LaunchedEffect(assembledIndices.size) {
        if (assembledIndices.size == word.length) {
            if (assembled.joinToString("") == word) {
                isWordAssembled = true
                speak(word)
            } else {
                shakeWrong = true
                delay(1500)
                shakeWrong = false
                assembledIndices = listOf()
            }
        }
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is ExerciseUiState.Success) {
            if (state.score < 100) {
                delay(3000)
                viewModel.reset()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(KidsBg)) {
        Box(
            modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(KidsOrange, KidsPink))).padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Text("←", fontSize = 24.sp, color = Color.White) }
                Text("Уровень ${level.number}", color = Color.White, fontWeight = FontWeight.Black)
            }
        }

        Column(
            modifier = Modifier.weight(1f).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            KidsGameMascot(text = mascotText)

            Row(modifier = Modifier.offset(x = (if (shakeWrong) 10 else 0).dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                word.forEachIndexed { idx, _ ->
                    val ch = assembled.getOrNull(idx)
                    WordTile(
                        char = ch?.toString() ?: "", 
                        isActive = ch != null,
                        onClick = {
                            if (ch != null && !isWordAssembled) {
                                assembledIndices = assembledIndices.filterIndexed { i, _ -> i != idx }
                            }
                        }
                    )
                }
            }

            if (!isWordAssembled) {
                FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    indexedLetters.forEachIndexed { i, pair ->
                        val isUsed = i in assembledIndices
                        LetterTile(pair.second.toString(), isUsed) {
                            if (!isUsed) {
                                assembledIndices = assembledIndices + i
                                speak(pair.second.toString())
                            }
                        }
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (uiState is ExerciseUiState.Success && (uiState as ExerciseUiState.Success).score == 100) {
                        Button(
                            onClick = onNextLevel,
                            colors = ButtonDefaults.buttonColors(containerColor = KidsMint),
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("СЛЕДУЮЩИЙ УРОВЕНЬ ▶", fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, KidsMint)
                        ) {
                            Text("ДОМОЙ 🏠", color = KidsMint, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.startRecording(word, ExerciseMode.WORD) },
                            modifier = Modifier.size(100.dp).clip(CircleShape).background(if (uiState is ExerciseUiState.Recording) KidsPink else KidsMint)
                        ) {
                            Text(if (uiState is ExerciseUiState.Recording) "⏹" else "🎙", fontSize = 40.sp, color = Color.White)
                        }
                        if (uiState is ExerciseUiState.Recording) Text("Слушаю...", color = KidsPink, fontWeight = FontWeight.Bold)
                        
                        if (uiState is ExerciseUiState.Success && (uiState as ExerciseUiState.Success).score < 100) {
                            Text("${(uiState as ExerciseUiState.Success).score}%", color = KidsOrange, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KidsGameMascot(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(KidsMintLight).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🦜", fontSize = 40.sp)
        Spacer(Modifier.width(12.dp))
        Text(text, fontWeight = FontWeight.Bold, color = KidsTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
fun WordTile(char: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp))
            .background(if (isActive) KidsMint else Color.White)
            .border(2.dp, if (isActive) KidsMintDark else KidsBorder, RoundedCornerShape(12.dp))
            .clickable(enabled = isActive) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(char, fontSize = 20.sp, fontWeight = FontWeight.Black, color = if (isActive) Color.White else KidsTextDisabled)
    }
}

@Composable
fun LetterTile(char: String, isUsed: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.padding(4.dp).size(52.dp).clip(RoundedCornerShape(14.dp))
            .background(if (isUsed) Color.LightGray else KidsOrange)
            .clickable(enabled = !isUsed, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (!isUsed) Text(char, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(modifier: Modifier, horizontalArrangement: Arrangement.Horizontal, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow(modifier = modifier, horizontalArrangement = horizontalArrangement) {
        content()
    }
}
