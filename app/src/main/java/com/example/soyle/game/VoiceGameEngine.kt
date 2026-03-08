package com.example.soyle.game

import com.example.soyle.domain.model.ExerciseMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Движок голосовых игр.
 * Отвечает за: текущий целевой звук, счёт игры, жизни, логику победы/поражения.
 */
class VoiceGameEngine {

    companion object {
        const val MAX_LIVES         = 3
        const val SCORE_PER_HIT     = 10
        const val SCORE_BONUS_COMBO = 5
        const val THRESHOLD_HIT     = 65   // score ≥ 65 → засчитываем попадание
    }

    // ── Состояние игры ────────────────────────────────────────────────────────

    data class GameState(
        val score        : Int     = 0,
        val lives        : Int     = MAX_LIVES,
        val combo        : Int     = 0,        // серия правильных ответов подряд
        val targetPhoneme: String  = "Р",
        val isGameOver   : Boolean = false,
        val isWon        : Boolean = false,
        val level        : Int     = 1,
        val roundsTotal  : Int     = 0,
        val roundsWon    : Int     = 0
    )

    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    // ── API ───────────────────────────────────────────────────────────────────

    /** Запустить новую игру */
    fun start(phoneme: String, level: Int = 1) {
        _state.value = GameState(
            targetPhoneme = phoneme,
            level         = level
        )
    }

    /**
     * Обработать результат произношения от сервера.
     * @param pronunciationScore оценка 0–100 от AI
     */
    fun onPronunciationResult(pronunciationScore: Int) {
        val current = _state.value
        if (current.isGameOver || current.isWon) return

        val isHit = pronunciationScore >= THRESHOLD_HIT

        val newCombo  = if (isHit) current.combo + 1 else 0
        val newLives  = if (isHit) current.lives else current.lives - 1
        val bonusScore = if (newCombo >= 3) SCORE_BONUS_COMBO else 0
        val newScore  = current.score + if (isHit) SCORE_PER_HIT + bonusScore else 0
        val newRoundsWon = current.roundsWon + if (isHit) 1 else 0
        val newTotal  = current.roundsTotal + 1

        val isGameOver = newLives <= 0
        val isWon      = newRoundsWon >= winsRequired(current.level)

        _state.value = current.copy(
            score       = newScore,
            lives       = newLives,
            combo       = newCombo,
            roundsTotal = newTotal,
            roundsWon   = newRoundsWon,
            isGameOver  = isGameOver,
            isWon       = isWon
        )
    }

    /** Перейти на следующий уровень */
    fun nextLevel() {
        val current = _state.value
        _state.value = GameState(
            score         = current.score,
            targetPhoneme = current.targetPhoneme,
            level         = current.level + 1
        )
    }

    /** Количество побед для прохождения уровня */
    fun winsRequired(level: Int) = 3 + (level - 1) * 2   // 3, 5, 7, 9...

    fun reset() { _state.value = GameState() }
}