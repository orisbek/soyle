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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.example.soyle.ui.theme.*

/**
 * 🎮 Игра «Поймай букву!»
 *
 * Механика:
 * - Сверху вниз падают буквы (нужная «Р» и отвлекающие)
 * - Ребёнок нажимает на букву «Р» — получает очки
 * - Нажимает неверную — теряет сердце
 * - Нужная буква падает и исчезает — теряет сердце
 * - 3 жизни, нарастающая скорость
 */

private const val GAME_WIDTH  = 360f
private const val GAME_HEIGHT = 580f
private const val LETTER_SIZE = 64f

private data class FallingLetter(
    val id       : Int,
    val letter   : String,
    val x        : Float,
    val y        : Float,
    val speed    : Float,
    val isTarget : Boolean,
    val caught   : Boolean = false,
    val missed   : Boolean = false
)

private data class GameState(
    val letters     : List<FallingLetter> = emptyList(),
    val score       : Int                 = 0,
    val lives       : Int                 = 3,
    val combo       : Int                 = 0,
    val level       : Int                 = 1,
    val ticks       : Int                 = 0,
    val isGameOver  : Boolean             = false,
    val isStarted   : Boolean             = false,
    val showCombo   : Boolean             = false,
    val lastEvent   : String              = ""
)

private val DISTRACTORS = listOf("Л", "С", "Ш", "З", "Ж", "Д", "Б", "В")

@Composable
fun GameScreen(
    onBack  : () -> Unit,
    onFinish: (score: Int) -> Unit = {}
) {
    var gameState by remember { mutableStateOf(GameState()) }
    var idCounter by remember { mutableIntStateOf(0) }
    var boxWidthPx  by remember { mutableFloatStateOf(1f) }
    var boxHeightPx by remember { mutableFloatStateOf(1f) }
    val density = LocalDensity.current

    // ── Игровой тик ──────────────────────────────────────────────────────────
    LaunchedEffect(gameState.isStarted, gameState.isGameOver) {
        if (!gameState.isStarted || gameState.isGameOver) return@LaunchedEffect

        while (true) {
            delay(30L)  // ~33 fps
            gameState = gameState.copy(ticks = gameState.ticks + 1).let { state ->
                val speedMultiplier = 1f + (state.level - 1) * 0.25f

                // Двигаем буквы вниз
                val moved = state.letters.map { letter ->
                    letter.copy(y = letter.y + letter.speed * speedMultiplier)
                }

                // Убираем пойманные/пропущенные
                val (active, gone) = moved.partition { it.y < boxHeightPx + LETTER_SIZE }

                // Потеря жизни за пропущенную целевую
                val missedTargets = gone.count { it.isTarget && !it.caught }
                val newLives      = (state.lives - missedTargets).coerceAtLeast(0)

                // Добавляем новую букву (каждые N тиков)
                val spawnInterval = maxOf(60 - state.level * 5, 25)
                val newLetters    = active.toMutableList()

                if (state.ticks % spawnInterval == 0) {
                    val isTarget = Random.nextFloat() < 0.45f
                    val letter   = if (isTarget) "Р" else DISTRACTORS.random()
                    val x        = Random.nextFloat() * (boxWidthPx - LETTER_SIZE * 2) + LETTER_SIZE / 2
                    val speed    = Random.nextFloat() * 3f + 3f
                    newLetters += FallingLetter(
                        id       = ++idCounter,
                        letter   = letter,
                        x        = x,
                        y        = -LETTER_SIZE,
                        speed    = speed,
                        isTarget = isTarget
                    )
                }

                // Повышение уровня каждые 10 очков
                val newLevel = 1 + state.score / 10

                state.copy(
                    letters    = newLetters,
                    lives      = newLives,
                    isGameOver = newLives <= 0,
                    level      = newLevel
                )
            }

            if (gameState.isGameOver) {
                delay(500)
                onFinish(gameState.score)
                break
            }
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
    ) {
        // ── Шапка ─────────────────────────────────────────────────────────────
        GameTopBar(
            score  = gameState.score,
            lives  = gameState.lives,
            level  = gameState.level,
            onBack = onBack
        )

        // ── Игровое поле ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onGloballyPositioned { coords ->
                    boxWidthPx  = coords.size.width.toFloat()
                    boxHeightPx = coords.size.height.toFloat()
                }
        ) {
            // Фоновые звёздочки
            repeat(20) { i ->
                val xFrac = (i * 0.13f + 0.05f) % 1f
                val yFrac = (i * 0.19f + 0.03f) % 1f
                Text(
                    text     = "✦",
                    fontSize = (8 + i % 6).sp,
                    color    = Color.White.copy(alpha = 0.1f + (i % 4) * 0.05f),
                    modifier = Modifier
                        .offset(
                            x = with(density) { (xFrac * boxWidthPx).toDp() },
                            y = with(density) { (yFrac * boxHeightPx).toDp() }
                        )
                )
            }

            // Падающие буквы
            gameState.letters.forEach { letter ->
                FallingLetterItem(
                    letter    = letter,
                    density   = density,
                    onTap     = { tapped ->
                        if (tapped.isTarget && !tapped.caught) {
                            val combo    = gameState.combo + 1
                            val points   = if (combo >= 3) 2 else 1
                            gameState = gameState.copy(
                                letters   = gameState.letters.map {
                                    if (it.id == tapped.id) it.copy(caught = true) else it
                                },
                                score     = gameState.score + points,
                                combo     = combo,
                                showCombo = combo >= 3,
                                lastEvent = if (combo >= 3) "КОМБО $combo! +$points" else "+$points ⭐"
                            )
                        } else if (!tapped.isTarget) {
                            gameState = gameState.copy(
                                lives     = (gameState.lives - 1).coerceAtLeast(0),
                                combo     = 0,
                                isGameOver = gameState.lives - 1 <= 0,
                                lastEvent = "Не та буква! 💔"
                            )
                        }
                    }
                )
            }

            // Подсказка «нажимай Р»
            if (!gameState.isStarted) {
                // показывается ниже
            }

            // Всплывающее событие
            if (gameState.lastEvent.isNotEmpty()) {
                Text(
                    text       = gameState.lastEvent,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = if (gameState.lastEvent.startsWith("+")) KidsYellow else KidsPink,
                    modifier   = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                )
            }
        }

        // ── Нижняя кнопка ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!gameState.isStarted) {
                StartButton {
                    gameState = GameState(isStarted = true)
                }
            } else if (gameState.isGameOver) {
                GameOverPanel(score = gameState.score) {
                    gameState = GameState(isStarted = true)
                    idCounter = 0
                }
            } else {
                // Подсказка во время игры
                Text(
                    text       = "Нажимай только на букву  Р!",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White.copy(alpha = 0.7f),
                    textAlign  = TextAlign.Center
                )
            }
        }
    }
}

// ── Падающая буква ────────────────────────────────────────────────────────────

@Composable
private fun FallingLetterItem(
    letter : FallingLetter,
    density: androidx.compose.ui.unit.Density,
    onTap  : (FallingLetter) -> Unit
) {
    if (letter.caught || letter.missed) return

    val scale by animateFloatAsState(
        targetValue = if (letter.caught) 1.6f else 1f,
        label       = "letterScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (letter.caught) 0f else 1f,
        label       = "letterAlpha"
    )

    val bgColor = if (letter.isTarget)
        Brush.radialGradient(colors = listOf(KidsMint, Color(0xFF00B5A3)))
    else
        Brush.radialGradient(colors = listOf(Color(0xFF6C757D), Color(0xFF495057)))

    val xDp = with(density) { letter.x.toDp() }
    val yDp = with(density) { letter.y.toDp() }

    Box(
        modifier = Modifier
            .offset(x = xDp - 32.dp, y = yDp - 32.dp)
            .size(64.dp)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(bgColor)
            .border(
                width = 3.dp,
                color = if (letter.isTarget) KidsYellow.copy(alpha = 0.8f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onTap(letter) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = letter.letter,
            fontSize   = 28.sp,
            fontWeight = FontWeight.Black,
            color      = Color.White,
            textAlign  = TextAlign.Center
        )
    }
}

// ── Шапка игры ────────────────────────────────────────────────────────────────

@Composable
private fun GameTopBar(score: Int, lives: Int, level: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Кнопка назад
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text("←", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Black)
        }

        // Очки
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("⭐", fontSize = 20.sp)
            Text(
                text       = score.toString(),
                fontSize   = 22.sp,
                fontWeight = FontWeight.Black,
                color      = KidsYellow
            )
        }

        // Жизни
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { i ->
                Text(
                    text     = if (i < lives) "❤️" else "🖤",
                    fontSize = 22.sp
                )
            }
        }

        // Уровень
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(KidsPurple.copy(alpha = 0.3f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text       = "Ур. $level",
                fontSize   = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = KidsPurple
            )
        }
    }
}

// ── Кнопка старта ─────────────────────────────────────────────────────────────

@Composable
private fun StartButton(onStart: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.05f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "startPulse"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = "🎮 Поймай букву Р!",
            fontSize   = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = Color.White,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text       = "Нажимай только когда\nвидишь букву  Р",
            fontSize   = 14.sp,
            color      = Color.White.copy(alpha = 0.7f),
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .scale(scale)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(KidsMint, KidsBlue)
                    )
                )
                .clickable(onClick = onStart)
                .padding(horizontal = 48.dp, vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "▶  НАЧАТЬ!",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Black,
                color      = Color.White
            )
        }
    }
}

// ── Экран конца игры ──────────────────────────────────────────────────────────

@Composable
private fun GameOverPanel(score: Int, onRestart: () -> Unit) {
    val stars = when {
        score >= 20 -> 3
        score >= 10 -> 2
        score >= 5  -> 1
        else        -> 0
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🎉", fontSize = 48.sp)
        Text(
            text       = "Игра окончена!",
            fontSize   = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = Color.White
        )
        Row {
            repeat(3) { i ->
                Text(
                    text     = if (i < stars) "⭐" else "☆",
                    fontSize = 32.sp
                )
            }
        }
        Text(
            text       = "Очки: $score",
            fontSize   = 28.sp,
            fontWeight = FontWeight.Black,
            color      = KidsYellow
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(KidsMint)
                .clickable(onClick = onRestart)
                .padding(horizontal = 40.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "🔄  Ещё раз!",
                fontSize   = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White
            )
        }
    }
}
