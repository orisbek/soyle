package com.example.soyle.game

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Игра «Летающий дракон».
 *
 * Механика:
 * - На экране летит дракон, перед ним препятствия (горы, облака).
 * - Ребёнок произносит целевой звук → дракон набирает высоту (прыгает).
 * - Если звук не произнесён вовремя → дракон падает вниз.
 * - Столкновение с препятствием → теряется жизнь.
 */
class DragonGame(private val engine: VoiceGameEngine = VoiceGameEngine()) {

    companion object {
        const val SCREEN_WIDTH      = 400f
        const val SCREEN_HEIGHT     = 600f
        const val DRAGON_X          = 80f
        const val GRAVITY           = 2.5f          // пикс/тик падение
        const val VOICE_LIFT        = -12f          // импульс при произношении
        const val OBSTACLE_SPEED    = 4f
        const val OBSTACLE_INTERVAL = 90            // тиков между препятствиями
        const val OBSTACLE_GAP      = 160f          // просвет между верхом и низом
        const val OBSTACLE_WIDTH    = 50f
    }

    // ── Модели ────────────────────────────────────────────────────────────────

    data class DragonState(
        val y          : Float   = SCREEN_HEIGHT / 2,
        val velocityY  : Float   = 0f,
        val isAlive    : Boolean = true
    )

    data class Obstacle(
        val x        : Float,
        val gapTop   : Float,     // y верхней границы просвета
        val gapBottom: Float,     // y нижней границы просвета
        val passed   : Boolean = false
    )

    data class GameFrame(
        val dragon      : DragonState   = DragonState(),
        val obstacles   : List<Obstacle> = emptyList(),
        val score       : Int           = 0,
        val lives       : Int           = VoiceGameEngine.MAX_LIVES,
        val isGameOver  : Boolean       = false,
        val ticks       : Int           = 0,
        val targetPhoneme: String       = "Р"
    )

    // ── State ─────────────────────────────────────────────────────────────────

    private val _frame = MutableStateFlow(GameFrame())
    val frame = _frame.asStateFlow()

    private var gameJob: Job? = null

    // ── Управление игрой ──────────────────────────────────────────────────────

    fun start(phoneme: String, scope: CoroutineScope) {
        engine.start(phoneme)
        _frame.value = GameFrame(targetPhoneme = phoneme)

        gameJob = scope.launch {
            while (_frame.value.dragon.isAlive && !_frame.value.isGameOver) {
                delay(16L)   // ~60 fps
                tick()
            }
        }
    }

    fun stop() { gameJob?.cancel() }

    /**
     * Вызывается когда ребёнок произнёс звук (получили score от сервера).
     */
    fun onVoiceInput(score: Int) {
        engine.onPronunciationResult(score)
        if (score >= VoiceGameEngine.THRESHOLD_HIT) {
            // Импульс вверх
            _frame.value = _frame.value.copy(
                dragon = _frame.value.dragon.copy(velocityY = VOICE_LIFT)
            )
        }
    }

    // ── Игровой тик ───────────────────────────────────────────────────────────

    private fun tick() {
        val current = _frame.value
        if (current.isGameOver) return

        // Обновляем дракона
        val newVelY  = current.dragon.velocityY + GRAVITY
        val newY     = (current.dragon.y + newVelY).coerceIn(0f, SCREEN_HEIGHT - 40f)
        val newDragon = current.dragon.copy(y = newY, velocityY = newVelY)

        // Обновляем препятствия
        var newObstacles = current.obstacles
            .map { it.copy(x = it.x - OBSTACLE_SPEED) }
            .filter { it.x + OBSTACLE_WIDTH > 0f }

        // Добавляем новое препятствие
        val newTicks = current.ticks + 1
        if (newTicks % OBSTACLE_INTERVAL == 0) {
            val gapTop = (SCREEN_HEIGHT * 0.2f) + (Math.random() * SCREEN_HEIGHT * 0.4f).toFloat()
            newObstacles = newObstacles + Obstacle(
                x         = SCREEN_WIDTH,
                gapTop    = gapTop,
                gapBottom = gapTop + OBSTACLE_GAP
            )
        }

        // Проверяем столкновения
        var newLives = current.lives
        var newScore = current.score
        var isGameOver = false

        for (obs in newObstacles) {
            val dragonRight = DRAGON_X + 40f
            val dragonBottom = newDragon.y + 40f

            if (dragonRight > obs.x && DRAGON_X < obs.x + OBSTACLE_WIDTH) {
                if (newDragon.y < obs.gapTop || dragonBottom > obs.gapBottom) {
                    newLives--
                    if (newLives <= 0) { isGameOver = true; break }
                }
            }
            // Засчитываем пройденное препятствие
            if (!obs.passed && obs.x + OBSTACLE_WIDTH < DRAGON_X) {
                newScore += 10
            }
        }

        _frame.value = current.copy(
            dragon    = newDragon,
            obstacles = newObstacles,
            score     = newScore,
            lives     = newLives,
            isGameOver = isGameOver,
            ticks     = newTicks
        )
    }
}