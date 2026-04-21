package com.example.soyle.data.repository

import android.util.Base64
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.entity.AttemptEntity
import com.example.soyle.data.remote.AnalyzeRequest
import com.example.soyle.data.remote.SoyleApi
import com.example.soyle.domain.model.*
import com.example.soyle.domain.repository.SpeechRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechRepositoryImpl @Inject constructor(
    private val attemptDao: AttemptDao,
    private val api: SoyleApi
) : SpeechRepository {

    override suspend fun analyzePronunciation(
        audioBytes : ByteArray,
        phoneme    : String,
        mode       : ExerciseMode
    ): Result<PronunciationResult> = withContext(Dispatchers.IO) {
        runCatching {
            if (audioBytes.isEmpty()) throw Exception("Аудио не записано")

            val audioBase64 = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
            
            val response = api.analyze(
                AnalyzeRequest(
                    audio_base64 = audioBase64,
                    phoneme = phoneme,
                    mode = mode.name
                )
            )

            // Специальная логика для буквы Р (диагностика замен на Л или Г)
            val (finalScore, finalFeedback) = if (phoneme.uppercase() == "Р") {
                when {
                    response.score >= 70 -> 100 to "Отлично! Ты правильно выговорил букву Р!"
                    response.score >= 15 -> 40 to "Ты близок! Попробуй еще раз сказать Р, а не Л или Г."
                    else -> 0 to "Неверно. Попробуй еще раз сказать Р!"
                }
            } else {
                response.score to response.feedback
            }

            PronunciationResult(
                score = finalScore,
                phoneme = phoneme,
                feedback = finalFeedback,
                durationMs = response.duration_ms,
                waveformData = response.waveform_data,
                referenceWave = emptyList(),
                xpEarned = if (finalScore >= 70) 20 else 5,
                mascotEmotion = if (finalScore >= 70) MascotEmotion.HAPPY else MascotEmotion.SUPPORTIVE
            )
        }
    }

    override suspend fun getDailyExercises(userId: String): List<Exercise> {
        return listOf(
            Exercise("1", "Р", ExerciseMode.SOUND, "Р", 1),
            Exercise("2", "Л", ExerciseMode.SOUND, "Л", 1),
            Exercise("3", "Г", ExerciseMode.SOUND, "Г", 1),
            Exercise("4", "Р", ExerciseMode.WORD, "РАК", 2),
        )
    }

    override suspend fun saveAttempt(
        userId: String,
        phoneme: String,
        mode: ExerciseMode,
        score: Int
    ) {
        attemptDao.insert(
            AttemptEntity(
                userId = userId,
                phoneme = phoneme,
                mode = mode.name,
                score = score
            )
        )
    }

    override fun getUserProgress(userId: String): Flow<UserProgress> =
        attemptDao.getAttemptsForUser(userId).map { attempts ->
            val totalXp = attempts.sumOf { (if (it.score >= 70) 20 else 5).toInt() }
            val phonemeScores = attempts.groupBy { it.phoneme }
                .mapValues { it.value.map { a -> a.score }.average().toFloat() }
            val totalSessions = attempts.map { it.timestamp / 86400000L }.distinct().size
            
            UserProgress(
                userId = userId,
                phonemeScores = phonemeScores,
                totalSessions = totalSessions,
                currentStreak = 0,
                longestStreak = 0,
                totalXp = totalXp,
                level = (totalXp / 500) + 1,
                achievements = emptyList()
            )
        }
}
