package com.example.soyle.domain.usecase

import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.domain.model.PronunciationResult
import com.example.soyle.domain.repository.SpeechRepository
import javax.inject.Inject

/**
 * Основной use case приложения.
 * Вся цепочка: аудио → сервер → сохранить → начислить XP.
 */
class AnalyzePronunciation @Inject constructor(
    private val repository: SpeechRepository
) {
    suspend operator fun invoke(
        audioBytes : ByteArray,
        phoneme    : String,
        mode       : ExerciseMode,
        userId     : String
    ): Result<PronunciationResult> {

        val result = repository.analyzePronunciation(audioBytes, phoneme, mode)

        // Сохраняем попытку независимо от результата (для статистики)
        result.onSuccess { pronunciationResult ->
            repository.saveAttempt(
                userId  = userId,
                phoneme = phoneme,
                mode    = mode,
                score   = pronunciationResult.score
            )
        }

        return result
    }
}