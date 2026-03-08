package com.example.soyle.domain.repository

import com.example.soyle.domain.model.Exercise
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.domain.model.PronunciationResult
import com.example.soyle.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface SpeechRepository {

    /**
     * Отправить аудио на сервер → получить оценку произношения.
     * @param audioBytes  сырые PCM-байты (16kHz, 16bit, mono)
     * @param phoneme     целевая фонема ("Р")
     * @param mode        режим упражнения
     */
    suspend fun analyzePronunciation(
        audioBytes : ByteArray,
        phoneme    : String,
        mode       : ExerciseMode
    ): Result<PronunciationResult>

    /** Дневные упражнения для пользователя */
    suspend fun getDailyExercises(userId: String): List<Exercise>

    /** Сохранить попытку локально (Room) */
    suspend fun saveAttempt(
        userId  : String,
        phoneme : String,
        mode    : ExerciseMode,
        score   : Int
    )

    /** Поток прогресса — автообновляет UI при изменениях */
    fun getUserProgress(userId: String): Flow<UserProgress>
}