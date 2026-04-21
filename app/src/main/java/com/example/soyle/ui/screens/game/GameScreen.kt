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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
 * 🎮 Улучшенная игра «Поймай букву!»
 */

private data class FallingLetter(
    val id       : Int,
    val letter   : String,
    val x        : Float,
    val y        : Float,
    val speed    : Float,
    val rotation : Float,
    val isTarget : Boolean,
    val isBonus  : Boolean = false,
    val caught   : Boolean = false
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

    // Анимация фона
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val bgOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "bgAnim"
    )

    LaunchedEffect(gameState.isStarted, gameState.isGameOver) {
        if (!gameState.isStarted || gameState.isGameOver) return@LaunchedEffect

        while (true) {
            delay(20L) // Плавнее - 50 FPS
            gameState = gameState.copy(ticks = gameState.ticks + 1).let { state ->
                val speedMultiplier = 1f + (state.level - 1) * 0.15f

                val moved = state.letters.map { letter ->
                    letter.copy(
                        y = letter.y + letter.speed * speedMultiplier,
                        rotation = letter.rotation + 2f
                    )
                }

                val (active, gone) = moved.partition { it.y < boxHeightPx + 100 }
                val missedTargets = gone.count { it.isTarget && !it.caught && !it.isBonus }
                val newLives = (state.lives - missedTargets).coerceAtLeast(0)

                val spawnInterval = maxOf(40 - state.level * 3, 15)
                val newLetters = active.toMutableList()

                if (state.ticks % spawnInterval == 0) {
                    val rand = Random.nextFloat()
                    val isBonus = rand < 0.05f
                    val isTarget = rand < 0.4f || isBonus
                    
                    val letter = if (isBonus) "🌟" else if (isTarget) "Р" else DISTRACTORS.random()
                    val x = Random.nextFloat() * (boxWidthPx - 150) + 75
                    
                    newLetters += FallingLetter(
                        id = ++idCounter,
                        letter = letter,
                        x = x,
                        y = -100f,
                        speed = Random.nextFloat() * 2f + 4f,
                        rotation = Random.nextFloat() * 360f,
                        isTarget = isTarget,
                        isBonus = isBonus
                    )
                }

                state.copy(
                    letters = newLetters,
                    lives = newLives,
                    isGameOver = newLives <= 0,
                    level = 1 + state.score / 15
                )
            }

            if (gameState.isGameOver) {
                delay(800)
                onFinish(gameState.score)
                break
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0C29))) {
        // Живой космический фон
        Canvas(modifier = Modifier.fillMaxSize()) {
            val brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
            )
            drawRect(brush)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            GameTopBar(score = gameState.score, lives = gameState.lives, level = gameState.level, onBack = onBack)

            Box(modifier = Modifier.weight(1f).fillMaxWidth().onGloballyPositioned {
                boxWidthPx = it.size.width.toFloat()
                boxHeightPx = it.size.height.toFloat()
            }) {
                // Буквы
                gameState.letters.forEach { letter ->
                    FallingLetterItem(
                        letter = letter,
                        onTap = { tapped ->
                            if (tapped.isTarget && !tapped.caught) {
                                val points = if (tapped.isBonus) 10 else 1
                                gameState = gameState.copy(
                                    letters = gameState.letters.map { if (it.id == tapped.id) it.copy(caught = true) else it },
                                    score = gameState.score + points,
                                    lastEvent = if (tapped.isBonus) "СУПЕР БОНУС! +10" else "+$points ⭐"
                                )
                            } else if (!tapped.isTarget) {
                                gameState = gameState.copy(
                                    lives = (gameState.lives - 1).coerceAtLeast(0),
                                    lastEvent = "Ой! Не та буква! 💔"
                                )
                            }
                        }
                    )
                }

                if (gameState.lastEvent.isNotEmpty()) {
                    Text(
                        text = gameState.lastEvent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = KidsYellow,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp)
                    )
                }
            }

            // Нижняя панель
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                if (!gameState.isStarted) {
                    StartButton { gameState = GameState(isStarted = true) }
                } else if (gameState.isGameOver) {
                    GameOverPanel(score = gameState.score) {
                        gameState = GameState(isStarted = true)
                        idCounter = 0
                    }
                }
            }
        }
    }
}

@Composable
private fun FallingLetterItem(
    letter : FallingLetter,
    onTap  : (FallingLetter) -> Unit
) {
    if (letter.caught) return

    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition(label = "letter")
    
    // Эффект покачивания
    val drift by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "drift"
    )

    val xDp = with(density) { (letter.x + drift).toDp() }
    val yDp = with(density) { letter.y.toDp() }

    Box(
        modifier = Modifier
            .offset(x = xDp - 35.dp, y = yDp - 35.dp)
            .size(70.dp)
            .rotate(letter.rotation)
            .clip(CircleShape)
            .background(
                if (letter.isBonus) Brush.radialGradient(listOf(Color.Yellow, KidsOrange))
                else if (letter.isTarget) Brush.linearGradient(listOf(KidsMint, KidsBlue))
                else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
            )
            .border(
                3.dp,
                if (letter.isTarget) Color.White.copy(0.6f) else Color.Transparent,
                CircleShape
            )
            .clickable { onTap(letter) },
        contentAlignment = Alignment.Center
    ) {
        if (letter.isTarget && !letter.isBonus) {
            // Эффект свечения для буквы Р
            Box(modifier = Modifier.fillMaxSize().blur(10.dp).background(Color.White.copy(0.2f)))
        }
        
        Text(
            text = letter.letter,
            fontSize = if (letter.isBonus) 32.sp else 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
private fun GameTopBar(score: Int, lives: Int, level: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.Black.copy(0.4f)).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Text("🏠", fontSize = 24.sp)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⭐", fontSize = 20.sp)
            Spacer(Modifier.width(4.dp))
            Text("$score", fontSize = 22.sp, fontWeight = FontWeight.Black, color = KidsYellow)
        }

        Row {
            repeat(3) { i ->
                Text(if (i < lives) "❤️" else "🖤", fontSize = 20.sp)
            }
        }

        Surface(color = KidsPurple, shape = RoundedCornerShape(8.dp)) {
            Text("УРОВЕНЬ $level", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun StartButton(onStart: () -> Unit) {
    Button(
        onClick = onStart,
        colors = ButtonDefaults.buttonColors(containerColor = KidsMint),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.height(60.dp).fillMaxWidth()
    ) {
        Text("ПОЕХАЛИ! 🚀", fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun GameOverPanel(score: Int, onRestart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("КОНЕЦ ИГРЫ!", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
        Text("Твой счёт: $score", fontSize = 24.sp, color = KidsYellow)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRestart, colors = ButtonDefaults.buttonColors(containerColor = KidsPink)) {
            Text("ЕЩЁ РАЗ 🔄", fontSize = 18.sp)
        }
    }
}
