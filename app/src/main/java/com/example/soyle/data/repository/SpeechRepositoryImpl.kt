package com.example.soyle.data.repository

import android.util.Base64
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.entity.AttemptEntity
import com.example.soyle.data.remote.AnalyzeRequest
import com.example.soyle.data.remote.SoyleApi
import com.example.soyle.domain.model.*
import com.example.soyle.domain.repository.SpeechRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechRepositoryImpl @Inject constructor(
    private val attemptDao: AttemptDao,
    private val api: SoyleApi,
    private val firestore: FirebaseFirestore
) : SpeechRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun analyzePronunciation(
        audioBytes: ByteArray,
        phoneme: String,
        mode: ExerciseMode
    ): Result<PronunciationResult> = withContext(Dispatchers.IO) {
        runCatching {
            if (audioBytes.isEmpty()) throw Exception("Аудио не записано")

            val audioBase64 = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
            val response = api.analyze(AnalyzeRequest(audio_base64 = audioBase64, phoneme = phoneme, mode = mode.name))

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

    override suspend fun getDailyExercises(userId: String): List<Exercise> = listOf(
        Exercise("1", "Р", ExerciseMode.SOUND, "Р", 1),
        Exercise("2", "Л", ExerciseMode.SOUND, "Л", 1),
        Exercise("3", "Г", ExerciseMode.SOUND, "Г", 1),
        Exercise("4", "Р", ExerciseMode.WORD, "РАК", 2),
    )

    override suspend fun saveAttempt(userId: String, phoneme: String, mode: ExerciseMode, score: Int) {
        attemptDao.insert(AttemptEntity(userId = userId, phoneme = phoneme, mode = mode.name, score = score))
        if (userId.isNotEmpty()) {
            try {
                updateFirestoreStats(userId, phoneme, score)
            } catch (_: Exception) {
                // Если Firestore недоступен — данные остаются в Room
            }
        }
    }

    override fun getUserProgress(userId: String): Flow<UserProgress> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyProgress(userId))
            close()
            return@callbackFlow
        }

        val docRef = firestore.collection("users").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val data = snapshot?.data
            trySend(if (data != null) parseUserProgress(userId, data) else emptyProgress(userId))
        }
        awaitClose { listener.remove() }
    }

    private suspend fun updateFirestoreStats(userId: String, phoneme: String, score: Int) {
        val docRef = firestore.collection("users").document(userId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val data = snapshot.data ?: return@runTransaction

            val today = dateFormat.format(Date())
            val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 86_400_000L))
            val lastSessionDate = data["lastSessionDate"] as? String ?: ""
            val currentStreak = (data["currentStreak"] as? Long)?.toInt() ?: 0
            val longestStreak = (data["longestStreak"] as? Long)?.toInt() ?: 0
            val totalSessions = (data["totalSessions"] as? Long)?.toInt() ?: 0
            val totalXp = (data["totalXp"] as? Long)?.toInt() ?: 0

            val xpEarned = if (score >= 70) 20 else 5
            val newTotalXp = totalXp + xpEarned
            val newLevel = (newTotalXp / 500) + 1

            val newStreak: Int
            val newSessions: Int
            when (lastSessionDate) {
                today -> {
                    newStreak = currentStreak
                    newSessions = totalSessions
                }
                yesterday -> {
                    newStreak = currentStreak + 1
                    newSessions = totalSessions + 1
                }
                else -> {
                    newStreak = 1
                    newSessions = totalSessions + 1
                }
            }
            val newLongest = maxOf(longestStreak, newStreak)

            @Suppress("UNCHECKED_CAST")
            val phonemeScores = (data["phonemeScores"] as? Map<String, Any>)
                ?.mapValues { it.value.toString().toFloat() }?.toMutableMap() ?: mutableMapOf()
            val oldScore = phonemeScores[phoneme] ?: 0f
            phonemeScores[phoneme] = if (oldScore == 0f) score.toFloat() else (oldScore + score) / 2f

            transaction.update(
                docRef, mapOf(
                    "totalXp" to newTotalXp,
                    "level" to newLevel,
                    "currentStreak" to newStreak,
                    "longestStreak" to newLongest,
                    "totalSessions" to newSessions,
                    "lastSessionDate" to today,
                    "phonemeScores" to phonemeScores
                )
            )
        }.await()
    }

    private fun parseUserProgress(uid: String, data: Map<String, Any>): UserProgress {
        @Suppress("UNCHECKED_CAST")
        val phonemeScores = (data["phonemeScores"] as? Map<String, Any>)
            ?.mapValues { it.value.toString().toFloat() } ?: emptyMap()
        return UserProgress(
            userId = uid,
            phonemeScores = phonemeScores,
            totalSessions = (data["totalSessions"] as? Long)?.toInt() ?: 0,
            currentStreak = (data["currentStreak"] as? Long)?.toInt() ?: 0,
            longestStreak = (data["longestStreak"] as? Long)?.toInt() ?: 0,
            totalXp = (data["totalXp"] as? Long)?.toInt() ?: 0,
            level = (data["level"] as? Long)?.toInt() ?: 1,
            achievements = emptyList()
        )
    }

    private fun emptyProgress(uid: String) = UserProgress(
        userId = uid,
        phonemeScores = emptyMap(),
        totalSessions = 0,
        currentStreak = 0,
        longestStreak = 0,
        totalXp = 0,
        level = 1,
        achievements = emptyList()
    )
}
