package com.example.soyle.data.repository

import com.example.soyle.audio.AudioProcessor
import com.example.soyle.data.api.AnalyzeRequest
import com.example.soyle.data.api.SoyleApi
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.dao.ExerciseDao
import com.example.soyle.data.local.entity.AttemptEntity
import com.example.soyle.data.local.entity.ExerciseEntity
import com.example.soyle.domain.model.*
import com.example.soyle.domain.repository.SpeechRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechRepositoryImpl @Inject constructor(
    private val api            : SoyleApi,
    private val attemptDao     : AttemptDao,
    private val exerciseDao    : ExerciseDao,
    private val audioProcessor : AudioProcessor
) : SpeechRepository {

    // ── Анализ произношения ───────────────────────────────────────────────────

    override suspend fun analyzePronunciation(
        audioBytes : ByteArray,
        phoneme    : String,
        mode       : ExerciseMode
    ): Result<PronunciationResult> = runCatching {

        // Проверяем — не тишина ли
        if (audioProcessor.isSilent(audioBytes)) {
            error("Ничего не записано. Говори громче!")
        }

        // Нормализуем громкость
        val normalized  = audioProcessor.normalize(audioBytes)
        val base64Audio = audioProcessor.toBase64(normalized)

        val response = api.analyze(
            AnalyzeRequest(
                audioBase64  = base64Audio,
                phoneme      = phoneme,
                userId       = "current_user",
                exerciseMode = mode.name.lowercase()
            )
        )

        // DTO → domain модель
        PronunciationResult(
            score         = response.score,
            phoneme       = response.phoneme,
            feedback      = response.feedback,
            durationMs    = response.durationMs,
            waveformData  = response.waveformData,
            referenceWave = response.referenceWaveform,
            xpEarned      = response.xpEarned,
            mascotEmotion = response.mascotEmotion.toMascotEmotion()
        )
    }

    // ── Дневные упражнения ────────────────────────────────────────────────────

    override suspend fun getDailyExercises(userId: String): List<Exercise> {
        // Сначала пробуем из кеша
        val cached = exerciseDao.getAll()
        if (cached.isNotEmpty()) return cached.map { it.toDomain() }

        // Если кеш пуст — загружаем с сервера
        return try {
            val remote = api.getDailyExercises(userId)
            val entities = remote.map {
                ExerciseEntity(
                    id         = it.id,
                    phoneme    = it.phoneme,
                    mode       = it.mode,
                    content    = it.content,
                    difficulty = it.difficulty
                )
            }
            exerciseDao.insertAll(entities)
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            // Нет интернета — возвращаем дефолтные упражнения
            defaultExercises()
        }
    }

    // ── Сохранить попытку ─────────────────────────────────────────────────────

    override suspend fun saveAttempt(
        userId  : String,
        phoneme : String,
        mode    : ExerciseMode,
        score   : Int
    ) {
        attemptDao.insert(
            AttemptEntity(
                userId    = userId,
                phoneme   = phoneme,
                mode      = mode.name,
                score     = score
            )
        )
    }

    // ── Прогресс пользователя ─────────────────────────────────────────────────

    override fun getUserProgress(userId: String): Flow<UserProgress> {
        return attemptDao.getByPhoneme(userId, "Р").map { attempts ->
            UserProgress(
                userId        = userId,
                phonemeScores = attempts
                    .groupBy { it.phoneme }
                    .mapValues { (_, list) -> list.map { it.score }.average().toFloat() },
                totalSessions = attempts.size,
                currentStreak = 0,   // TODO: вычислять из дат
                longestStreak = 0,
                totalXp       = attempts.sumOf { it.score },
                level         = attempts.sumOf { it.score } / 500 + 1,
                achievements  = emptyList()
            )
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun ExerciseEntity.toDomain() = Exercise(
        id         = id,
        phoneme    = phoneme,
        mode       = ExerciseMode.valueOf(mode),
        content    = content,
        difficulty = difficulty
    )

    private fun String.toMascotEmotion() = when (this) {
        "happy"       -> MascotEmotion.HAPPY
        "good"        -> MascotEmotion.GOOD
        "neutral"     -> MascotEmotion.NEUTRAL
        "supportive"  -> MascotEmotion.SUPPORTIVE
        "celebrating" -> MascotEmotion.CELEBRATING
        else          -> MascotEmotion.NEUTRAL
    }

    private fun defaultExercises() = listOf(
        Exercise("1", "Р", ExerciseMode.SOUND,    "Р",     1),
        Exercise("2", "Р", ExerciseMode.SYLLABLE, "РА",    1),
        Exercise("3", "Р", ExerciseMode.SYLLABLE, "РО",    1),
        Exercise("4", "Р", ExerciseMode.WORD,     "РЫБА",  2),
        Exercise("5", "Р", ExerciseMode.WORD,     "РУКА",  2),
        Exercise("6", "Л", ExerciseMode.SOUND,    "Л",     1),
        Exercise("7", "Л", ExerciseMode.SYLLABLE, "ЛА",    1),
        Exercise("8", "Л", ExerciseMode.WORD,     "ЛИСА",  2),
    )
}