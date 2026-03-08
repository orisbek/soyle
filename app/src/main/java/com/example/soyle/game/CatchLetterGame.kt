package com.example.soyle.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * Игра «Поймай букву».
 *
 * Механика:
 * - На экране падают буквы (целевые + отвлекающие).
 * - Ребёнок должен произнести звук когда падает НУЖНАЯ буква.
 * - Произнёс вовремя → буква поймана, +очки.
 * - Произнёс не ту букву → штраф.
 * - Пропустил нужную → теряется жизнь.
 */
class CatchLetterGame {

    companion object {
        const val SCREEN_WIDTH   = 400f
        const val SCREEN_HEIGHT  = 700f
        const val LETTER_SIZE    = 60f
        const val FALL_SPEED_MIN = 2f
        const val FALL_SPEED_MAX = 6f
        const val SPAWN_INTERVAL = 60      // тиков между появлением букв
        val DISTRACTOR_PHONEMES  = listOf("Л", "Ш", "С", "З", "Ж", "Ц", "Ч")
    }

    // ── Модели ────────────────────────────────────────────────────────────────

    data class FallingLetter(
        val id       : Int,
        val letter   : String,
        val x        : Float,
        val y        : Float,
        val speed    : Float,
        val isTarget : Boolean,   // true = нужно поймать
        val isCaught : Boolean = false,
        val isMissed : Boolean = false
    )

    data class GameState(
        val targetPhoneme : String              = "Р",
        val letters       : List<FallingLetter> = emptyList(),
        val score         : Int                 = 0,
        val lives         : Int                 = 3,
        val combo         : Int                 = 0,
        val ticks         : Int                 = 0,
        val isGameOver    : Boolean             = false,
        val lastEvent     : GameEvent           = GameEvent.NONE
    )

    enum class GameEvent { NONE, CAUGHT, MISSED, FALSE_ALARM }

    // ── State ─────────────────────────────────────────────────────────────────

    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    private var nextId = 0

    // ── API ───────────────────────────────────────────────────────────────────

    fun start(phoneme: String) {
        _state.value = GameState(targetPhoneme = phoneme)
        nextId = 0
    }

    /** Игровой тик — вызывать 60 раз в секунду */
    fun tick() {
        val current = _state.value
        if (current.isGameOver) return

        // Двигаем буквы вниз
        val moved = current.letters.map {
            it.copy(y = it.y + it.speed)
        }

        // Отмечаем пропущенные (вышли за экран)
        var lives = current.lives
        var event = GameEvent.NONE

        val processed = moved.map { letter ->
            if (!letter.isCaught && !letter.isMissed && letter.y > SCREEN_HEIGHT) {
                if (letter.isTarget) {
                    lives--
                    event = GameEvent.MISSED
                }
                letter.copy(isMissed = true)
            } else letter
        }.filter { !it.isMissed }

        // Спавн новой буквы
        val newTicks = current.ticks + 1
        val newLetters = if (newTicks % SPAWN_INTERVAL == 0) {
            processed + spawnLetter(current.targetPhoneme)
        } else processed

        _state.value = current.copy(
            letters    = newLetters,
            lives      = lives,
            ticks      = newTicks,
            isGameOver = lives <= 0,
            lastEvent  = event
        )
    }

    /**
     * Ребёнок произнёс звук — проверяем, есть ли целевая буква в нижней трети экрана.
     */
    fun onVoiceInput(pronunciationScore: Int) {
        val current = _state.value
        val isGoodPronunciation = pronunciationScore >= 65

        // Ищем целевую букву в зоне поимки (нижние 40% экрана)
        val catchZoneTop = SCREEN_HEIGHT * 0.6f
        val targetInZone = current.letters.find {
            it.isTarget && !it.isCaught && it.y >= catchZoneTop
        }

        val newState = when {
            targetInZone != null && isGoodPronunciation -> {
                // Поймали!
                val newCombo = current.combo + 1
                val bonusScore = if (newCombo >= 3) 15 else 0
                current.copy(
                    letters   = current.letters.map {
                        if (it.id == targetInZone.id) it.copy(isCaught = true) else it
                    }.filter { !it.isCaught },
                    score     = current.score + 10 + bonusScore,
                    combo     = newCombo,
                    lastEvent = GameEvent.CAUGHT
                )
            }
            targetInZone == null && isGoodPronunciation -> {
                // Ложная тревога — произнёс звук, но нужной буквы нет
                current.copy(
                    score     = (current.score - 5).coerceAtLeast(0),
                    combo     = 0,
                    lastEvent = GameEvent.FALSE_ALARM
                )
            }
            else -> current.copy(lastEvent = GameEvent.NONE)
        }

        _state.value = newState
    }

    fun reset() {
        _state.value = GameState()
        nextId = 0
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun spawnLetter(targetPhoneme: String): FallingLetter {
        // 40% шанс целевой буквы, 60% — отвлекающей
        val isTarget = Random.nextFloat() < 0.4f
        val letter   = if (isTarget) targetPhoneme
        else DISTRACTOR_PHONEMES.random()

        return FallingLetter(
            id       = nextId++,
            letter   = letter,
            x        = Random.nextFloat() * (SCREEN_WIDTH - LETTER_SIZE),
            y        = -LETTER_SIZE,
            speed    = FALL_SPEED_MIN + Random.nextFloat() * (FALL_SPEED_MAX - FALL_SPEED_MIN),
            isTarget = isTarget
        )
    }
}