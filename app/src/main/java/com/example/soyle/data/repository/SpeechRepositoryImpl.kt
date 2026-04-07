package com.example.soyle.data.repository

import android.util.Base64
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.entity.AttemptEntity
import com.example.soyle.domain.model.*
import com.example.soyle.domain.repository.SpeechRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechRepositoryImpl @Inject constructor(
    private val attemptDao: AttemptDao
) : SpeechRepository {

    // В реальном проекте вынести в BuildConfig / секреты
    private val baseUrl = "http://10.0.2.2:8000"  // localhost для эмулятора

    override suspend fun analyzePronunciation(
        audioBytes : ByteArray,
        phoneme    : String,
        mode       : ExerciseMode
    ): Result<PronunciationResult> = runCatching {

        val audioBase64 = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

        val requestBody = JSONObject().apply {
            put("audio_base64", audioBase64)
            put("phoneme", phoneme)
            put("mode", mode.name)
        }.toString()

        val url = URL("$baseUrl/analyze")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod    = "POST"
            doOutput         = true
            connectTimeout   = 10_000
            readTimeout      = 15_000
            setRequestProperty("Content-Type", "application/json")
        }

        conn.outputStream.use { it.write(requestBody.toByteArray()) }

        val responseCode = conn.responseCode
        val responseBody = if (responseCode == 200) {
            conn.inputStream.bufferedReader().readText()
        } else {
            conn.errorStream?.bufferedReader()?.readText()
                ?: "HTTP error $responseCode"
        }
        conn.disconnect()

        if (responseCode != 200) {
            throw Exception("Server error $responseCode: $responseBody")
        }

        parsePronunciationResult(responseBody, phoneme)
    }

    private fun parsePronunciationResult(json: String, phoneme: String): PronunciationResult {
        val obj      = JSONObject(json)
        val score    = obj.getInt("score")
        val feedback = obj.optString("feedback", "Молодец!")

        // waveform — опционально
        val waveformArr = obj.optJSONArray("waveform_data")
        val waveform    = if (waveformArr != null) {
            (0 until waveformArr.length()).map { waveformArr.getDouble(it).toFloat() }
        } else emptyList()

        val refArr    = obj.optJSONArray("reference_wave")
        val refWave   = if (refArr != null) {
            (0 until refArr.length()).map { refArr.getDouble(it).toFloat() }
        } else emptyList()

        val xpEarned = when {
            score >= 85 -> 20
            score >= 65 -> 15
            score >= 45 -> 10
            else        -> 5
        }
        val emotion = when {
            score >= 85 -> MascotEmotion.HAPPY
            score >= 65 -> MascotEmotion.GOOD
            score >= 45 -> MascotEmotion.NEUTRAL
            else        -> MascotEmotion.SUPPORTIVE
        }

        return PronunciationResult(
            score         = score,
            phoneme       = phoneme,
            feedback      = feedback,
            durationMs    = obj.optInt("duration_ms", 0),
            waveformData  = waveform,
            referenceWave = refWave,
            xpEarned      = xpEarned,
            mascotEmotion = emotion
        )
    }

    override suspend fun getDailyExercises(userId: String): List<Exercise> {
        // Жёстко закодированный набор на старте.
        // В следующей версии — запрос к серверу с учётом прогресса пользователя.
        return listOf(
            Exercise("1", "Р", ExerciseMode.SOUND,         "Р",      1),
            Exercise("2", "Р", ExerciseMode.SYLLABLE,      "РА",     1),
            Exercise("3", "Р", ExerciseMode.SYLLABLE,      "РО",     1),
            Exercise("4", "Р", ExerciseMode.WORD,          "РЫБА",   2),
            Exercise("5", "Р", ExerciseMode.WORD,          "ТРАВА",  2),
            Exercise("6", "Р", ExerciseMode.LISTEN_CHOOSE, "РАКЕТА", 2),
            Exercise("7", "Р", ExerciseMode.GAME,          "ИГРА",   3),
            Exercise("8", "Л", ExerciseMode.SOUND,         "Л",      1),
            Exercise("9", "Л", ExerciseMode.SYLLABLE,      "ЛА",     1),
            Exercise("10","Л", ExerciseMode.WORD,          "ЛИСА",   2),
        )
    }

    override suspend fun saveAttempt(
        userId  : String,
        phoneme : String,
        mode    : ExerciseMode,
        score   : Int
    ) {
        attemptDao.insert(
            AttemptEntity(
                userId  = userId,
                phoneme = phoneme,
                mode    = mode.name,
                score   = score
            )
        )
    }

    override fun getUserProgress(userId: String): Flow<UserProgress> =
        attemptDao.getAttemptsForUser(userId).map { attempts ->
            // Считаем средние баллы по каждой фонеме
            val phonemeScores = attempts
                .groupBy { it.phoneme }
                .mapValues { (_, list) -> list.map { it.score }.average().toFloat() }

            // Streak: считаем дни подряд
            val daysSorted = attempts
                .map { it.timestamp / 86_400_000L }  // перевод в дни
                .distinct()
                .sortedDescending()

            var streak = 0
            val today = System.currentTimeMillis() / 86_400_000L
            daysSorted.forEachIndexed { i, day ->
                if (day == today - i) streak++ else return@forEachIndexed
            }

            val totalXp     = attempts.sumOf { xpForScore(it.score) }
            val level       = (totalXp / 500) + 1
            val totalSessions = attempts
                .map { it.timestamp / 86_400_000L }
                .distinct()
                .size

            UserProgress(
                userId        = userId,
                phonemeScores = phonemeScores,
                totalSessions = totalSessions,
                currentStreak = streak,
                longestStreak = streak,   // TODO: хранить отдельно
                totalXp       = totalXp,
                level         = level,
                achievements  = buildAchievements(phonemeScores, streak, totalXp)
            )
        }

    private fun xpForScore(score: Int) = when {
        score >= 85 -> 20
        score >= 65 -> 15
        score >= 45 -> 10
        else        -> 5
    }

    private fun buildAchievements(
        phonemeScores: Map<String, Float>,
        streak: Int,
        totalXp: Int
    ): List<Achievement> = listOf(
        Achievement(
            id          = "first_session",
            title       = "Первый шаг",
            description = "Выполни первое упражнение",
            isUnlocked  = totalXp > 0
        ),
        Achievement(
            id          = "streak_3",
            title       = "3 дня подряд 🔥",
            description = "Занимайся 3 дня без пропусков",
            isUnlocked  = streak >= 3
        ),
        Achievement(
            id          = "streak_7",
            title       = "Неделя без пропусков 🏆",
            description = "Занимайся 7 дней подряд",
            isUnlocked  = streak >= 7
        ),
        Achievement(
            id          = "master_r",
            title       = "Мастер звука Р",
            description = "Достигни 85% точности по звуку Р",
            isUnlocked  = (phonemeScores["Р"] ?: 0f) >= 85f
        ),
        Achievement(
            id          = "level_5",
            title       = "Уровень 5",
            description = "Набери 2000 XP",
            isUnlocked  = totalXp >= 2000
        )
    )
}
