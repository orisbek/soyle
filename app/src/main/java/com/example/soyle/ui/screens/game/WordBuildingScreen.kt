package com.example.soyle.ui.screens.game

import android.Manifest
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.ui.screens.exercise.ExerciseUiState
import com.example.soyle.ui.screens.exercise.ExerciseViewModel
import com.example.soyle.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

// ── Данные слов (3 уровня сложности) ─────────────────────────────────────────

data class PronounceWord(val word: String, val hint: String)

private val WORDS_LEVEL_1 = listOf(
    PronounceWord("рак",   "Живёт в воде"),
    PronounceWord("рот",   "Говорит и ест"),
    PronounceWord("рог",   "На голове у оленя"),
    PronounceWord("рис",   "Белая крупа"),
    PronounceWord("рысь",  "Быстрая кошка"),
    PronounceWord("рука",  "Часть тела"),
    PronounceWord("рыба",  "Плавает в реке"),
    PronounceWord("роза",  "Красивый цветок"),
    PronounceWord("рост",  "Высота человека"),
    PronounceWord("мир",   "Спокойствие"),
    PronounceWord("сыр",   "Жёлтый и вкусный"),
    PronounceWord("шар",   "Круглый и воздушный"),
    PronounceWord("пар",   "Горячий туман"),
    PronounceWord("жар",   "Очень жарко"),
    PronounceWord("дар",   "Подарок, талант")
)

private val WORDS_LEVEL_2 = listOf(
    PronounceWord("радуга",   "Цветной мостик в небе"),
    PronounceWord("работа",   "Взрослые туда ходят"),
    PronounceWord("рыбалка",  "Ловят рыбу"),
    PronounceWord("рубашка",  "Одежда с пуговицами"),
    PronounceWord("ремонт",   "Починить дом"),
    PronounceWord("ребёнок",  "Маленький человек"),
    PronounceWord("рисунок",  "Нарисованная картинка"),
    PronounceWord("радость",  "Счастливое чувство"),
    PronounceWord("ромашка",  "Белый полевой цветок"),
    PronounceWord("береза",   "Белое дерево"),
    PronounceWord("морковь",  "Оранжевый овощ"),
    PronounceWord("зеркало",  "Смотрят своё лицо"),
    PronounceWord("горка",    "Дети катаются"),
    PronounceWord("трамвай",  "Едет по рельсам"),
    PronounceWord("пожар",    "Огонь везде"),
)

private val WORDS_LEVEL_3 = listOf(
    PronounceWord("рыболов",     "Тот кто ловит рыбу"),
    PronounceWord("ракетка",     "Для игры в теннис"),
    PronounceWord("рукопись",    "Написанная от руки"),
    PronounceWord("развитие",    "Учёба и рост"),
    PronounceWord("рассказ",     "Короткая история"),
    PronounceWord("разговор",    "Когда люди говорят"),
    PronounceWord("радоваться",  "Чувствовать радость"),
    PronounceWord("мороженое",   "Холодное и сладкое"),
    PronounceWord("коридор",     "Проход в доме"),
    PronounceWord("руководство", "Главная инструкция"),
    PronounceWord("различать",   "Отличать одно от другого"),
    PronounceWord("разбудить",   "Разбудить ото сна"),
    PronounceWord("рыбачить",    "Ловить рыбу удочкой"),
    PronounceWord("разложить",   "Разложить по местам"),
    PronounceWord("рабочий",     "Тот кто работает"),
)

private fun wordsForLevel(level: Int) = when (level) {
    1    -> WORDS_LEVEL_1
    2    -> WORDS_LEVEL_2
    else -> WORDS_LEVEL_3
}

// ── Вспомогательный enum уровней ──────────────────────────────────────────────

private enum class WordsLevel { SELECT, LEVEL1, LEVEL2, LEVEL3 }

// ── Точка входа ───────────────────────────────────────────────────────────────

@Composable
fun WordBuildingScreen(onBack: () -> Unit = {}) {
    var level by remember { mutableStateOf(WordsLevel.SELECT) }

    AnimatedContent(
        targetState    = level,
        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
        label          = "wordsLevel"
    ) { current ->
        when (current) {
            WordsLevel.SELECT -> WordsLevelSelect(
                onBack   = onBack,
                onSelect = { level = it }
            )
            WordsLevel.LEVEL1 -> WordsPronounceGame(
                levelNum  = 1,
                onBack    = { level = WordsLevel.SELECT }
            )
            WordsLevel.LEVEL2 -> WordsPronounceGame(
                levelNum  = 2,
                onBack    = { level = WordsLevel.SELECT }
            )
            WordsLevel.LEVEL3 -> WordsPronounceGame(
                levelNum  = 3,
                onBack    = { level = WordsLevel.SELECT }
            )
        }
    }
}

// ── Выбор уровня ──────────────────────────────────────────────────────────────

@Composable
private fun WordsLevelSelect(
    onBack   : () -> Unit,
    onSelect : (WordsLevel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад", tint = SoyleTextPrimary)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text       = "Слова на «Р»",
            fontWeight = FontWeight.Bold,
            fontSize   = 28.sp,
            color      = SoyleTextPrimary
        )
        Text(
            text     = "Произнеси слово — получи очки",
            fontSize = 14.sp,
            color    = SoyleTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        WordsLevelCard(
            number      = 1,
            title       = "Лёгкий",
            description = "Короткие слова — рак, рот, рука…",
            color       = Color(0xFF4CAF50),
            onClick     = { onSelect(WordsLevel.LEVEL1) }
        )
        Spacer(Modifier.height(14.dp))
        WordsLevelCard(
            number      = 2,
            title       = "Средний",
            description = "Средние слова — радуга, ромашка…",
            color       = SoyleAccent,
            onClick     = { onSelect(WordsLevel.LEVEL2) }
        )
        Spacer(Modifier.height(14.dp))
        WordsLevelCard(
            number      = 3,
            title       = "Сложный",
            description = "Длинные слова — разговор, рукопись…",
            color       = Color(0xFFE53935),
            onClick     = { onSelect(WordsLevel.LEVEL3) }
        )
    }
}

@Composable
internal fun WordsLevelCard(
    number      : Int,
    title       : String,
    description : String,
    color       : Color,
    onClick     : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = number.toString(),
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = color
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title,       fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = SoyleTextPrimary)
            Text(text = description, fontSize = 12.sp, color = SoyleTextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
        Icon(
            imageVector        = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint               = SoyleTextMuted,
            modifier           = Modifier.size(20.dp)
        )
    }
}

// ── Игровой экран произношения ────────────────────────────────────────────────

@Composable
private fun WordsPronounceGame(
    levelNum  : Int,
    onBack    : () -> Unit,
    viewModel : ExerciseViewModel = hiltViewModel()
) {
    val context  = LocalContext.current
    val uiState  by viewModel.uiState.collectAsState()
    val words    = remember(levelNum) { wordsForLevel(levelNum).shuffled() }

    // TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val t = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale("ru", "RU")
        }
        tts = t
        onDispose { t.shutdown() }
    }

    // Состояние игры
    var lives        by remember { mutableIntStateOf(3) }
    var score        by remember { mutableIntStateOf(0) }
    var currentIdx   by remember { mutableIntStateOf(0) }
    var totalScore   by remember { mutableIntStateOf(0) }  // сумма очков за произношение
    var gameOver     by remember { mutableStateOf(false) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    val total       = words.size
    val currentWord = if (currentIdx < total) words[currentIdx] else null

    // Автоозвучка при смене слова
    LaunchedEffect(currentIdx) {
        viewModel.reset()
        if (currentWord != null) {
            delay(400)
            tts?.speak(currentWord.word, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // Обработка результата распознавания
    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is ExerciseUiState.Success -> {
                totalScore += s.score
                if (s.score >= 50) score++ else lives--
                delay(1800)
                if (lives <= 0 || currentIdx + 1 >= total) {
                    gameOver = true
                } else {
                    currentIdx++
                }
            }
            is ExerciseUiState.Error -> {
                lives--
                delay(1500)
                if (lives <= 0 || currentIdx + 1 >= total) {
                    gameOver = true
                } else {
                    currentIdx++
                }
            }
            else -> Unit
        }
    }

    fun restart() {
        lives      = 3
        score      = 0
        currentIdx = 0
        totalScore = 0
        gameOver   = false
        viewModel.reset()
    }

    when {
        gameOver -> GameOverScreen(
            score     = score,
            onBack    = onBack,
            onRestart = ::restart
        )
        currentWord == null -> Box(Modifier.fillMaxSize().background(SoyleBg), Alignment.Center) {
            Text("Нет слов", color = SoyleTextSecondary)
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoyleBg)
            ) {
                GameTopBar(
                    title  = "Слова на «Р»",
                    lives  = lives,
                    score  = score,
                    onBack = onBack
                )

                LinearProgressIndicator(
                    progress   = { (currentIdx + 1).toFloat() / total },
                    modifier   = Modifier.fillMaxWidth().height(3.dp),
                    color      = SoyleAccent,
                    trackColor = SoyleSurface2
                )

                Spacer(Modifier.weight(1f))

                // Подсказка
                Text(
                    text      = currentWord.hint,
                    fontSize  = 13.sp,
                    color     = SoyleTextMuted,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                // Слово с подсветкой буквы Р
                WordCardDisplay(word = currentWord.word)

                Spacer(Modifier.height(28.dp))

                // Кнопки действий
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Послушать
                    ActionButton(
                        icon        = Icons.Outlined.VolumeUp,
                        label       = "Послушать",
                        color       = SoyleAccent,
                        enabled     = uiState is ExerciseUiState.Idle || uiState is ExerciseUiState.Success || uiState is ExerciseUiState.Error,
                        onClick     = {
                            tts?.speak(currentWord.word, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    )

                    // Говорить / Запись
                    val isRecording  = uiState is ExerciseUiState.Recording
                    val isAnalyzing  = uiState is ExerciseUiState.Analyzing
                    val micColor     = when {
                        isRecording -> Color(0xFFE53935)
                        isAnalyzing -> SoyleTextMuted
                        else        -> Color(0xFF4CAF50)
                    }
                    val micIcon  = if (isRecording) Icons.Outlined.Stop else Icons.Outlined.Mic
                    val micLabel = when {
                        isRecording -> "Стоп"
                        isAnalyzing -> "Анализ…"
                        else        -> "Говори!"
                    }
                    ActionButton(
                        icon    = micIcon,
                        label   = micLabel,
                        color   = micColor,
                        enabled = !isAnalyzing,
                        size    = 72.dp,
                        onClick = {
                            if (!hasPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                return@ActionButton
                            }
                            if (isRecording) {
                                viewModel.stopRecording()
                            } else {
                                viewModel.startRecording(
                                    phoneme = currentWord.word.lowercase(),
                                    mode    = ExerciseMode.WORD
                                )
                            }
                        }
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Результат произношения
                AnimatedContent(
                    targetState    = uiState,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label          = "resultBanner",
                    modifier       = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                ) { state ->
                    when (state) {
                        is ExerciseUiState.Success -> ResultBanner(
                            score    = state.score,
                            feedback = state.feedback
                        )
                        is ExerciseUiState.Error -> ErrorBanner(message = state.message)
                        is ExerciseUiState.Analyzing -> AnalyzingBanner()
                        else -> Spacer(Modifier.height(56.dp))
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text      = "${currentIdx + 1} / $total",
                    fontSize  = 12.sp,
                    color     = SoyleTextMuted,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ── Отображение слова с подсветкой Р ─────────────────────────────────────────

@Composable
private fun WordCardDisplay(word: String) {
    val rIndex = word.indexOfFirst { it.lowercaseChar() == 'р' }
    val annotated = buildAnnotatedString {
        word.forEachIndexed { i, ch ->
            if (i == rIndex) {
                withStyle(SpanStyle(color = SoyleAccent, fontWeight = FontWeight.ExtraBold)) {
                    append(ch.uppercaseChar().toString())
                }
            } else {
                withStyle(SpanStyle(color = SoyleTextPrimary)) {
                    append(ch.uppercaseChar().toString())
                }
            }
        }
    }
    Box(
        modifier         = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SoyleSurface)
                .border(1.5.dp, SoyleBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 40.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text          = annotated,
                fontSize      = 38.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }
    }
}

// ── Кнопка действия ──────────────────────────────────────────────────────────

@Composable
private fun ActionButton(
    icon    : androidx.compose.ui.graphics.vector.ImageVector,
    label   : String,
    color   : Color,
    enabled : Boolean,
    size    : androidx.compose.ui.unit.Dp = 60.dp,
    onClick : () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue   = if (enabled) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "btnScale"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(size)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(2.dp, color.copy(alpha = if (enabled) 0.5f else 0.2f), CircleShape)
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = if (enabled) color else color.copy(alpha = 0.4f),
                modifier           = Modifier.size(size * 0.45f)
            )
        }
        Text(
            text      = label,
            fontSize  = 11.sp,
            color     = if (enabled) SoyleTextSecondary else SoyleTextMuted,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// ── Баннеры результата ────────────────────────────────────────────────────────

@Composable
private fun ResultBanner(score: Int, feedback: String) {
    val isGood  = score >= 50
    val bgColor = if (isGood) Color(0xFF4CAF50).copy(alpha = 0.12f) else Color(0xFFFF9800).copy(alpha = 0.12f)
    val bdColor = if (isGood) Color(0xFF4CAF50).copy(alpha = 0.4f)  else Color(0xFFFF9800).copy(alpha = 0.4f)
    val icon    = if (isGood) Icons.Outlined.CheckCircle else Icons.Outlined.Info

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, bdColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = if (isGood) Color(0xFF4CAF50) else Color(0xFFFF9800),
            modifier           = Modifier.size(22.dp)
        )
        Column {
            Text(
                text       = if (isGood) "Отлично! $score / 100" else "Ещё раз! $score / 100",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = SoyleTextPrimary
            )
            if (feedback.isNotBlank()) {
                Text(text = feedback, fontSize = 12.sp, color = SoyleTextSecondary)
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE53935).copy(alpha = 0.10f))
            .border(1.dp, Color(0xFFE53935).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = Icons.Outlined.MicOff,
            contentDescription = null,
            tint               = Color(0xFFE53935),
            modifier           = Modifier.size(22.dp)
        )
        Text(
            text     = message.take(60),
            fontSize = 13.sp,
            color    = SoyleTextSecondary
        )
    }
}

@Composable
private fun AnalyzingBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(
            modifier  = Modifier.size(18.dp),
            color     = SoyleAccent,
            strokeWidth = 2.dp
        )
        Text(
            text     = "Анализирую произношение…",
            fontSize = 13.sp,
            color    = SoyleTextSecondary
        )
    }
}
