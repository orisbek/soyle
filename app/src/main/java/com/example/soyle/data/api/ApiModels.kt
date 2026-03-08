package com.example.soyle.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Запрос к POST /analyze ────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class AnalyzeRequest(
    @Json(name = "audio_base64")   val audioBase64  : String,
    @Json(name = "phoneme")        val phoneme      : String,
    @Json(name = "user_id")        val userId       : String,
    @Json(name = "exercise_mode")  val exerciseMode : String
)

// ── Ответ от POST /analyze ────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class AnalyzeResponse(
    @Json(name = "score")               val score             : Int,
    @Json(name = "phoneme")             val phoneme           : String,
    @Json(name = "feedback")            val feedback          : String,
    @Json(name = "duration_ms")         val durationMs        : Int,
    @Json(name = "waveform_data")       val waveformData      : List<Float>,
    @Json(name = "reference_waveform")  val referenceWaveform : List<Float>,
    @Json(name = "xp_earned")           val xpEarned          : Int,
    @Json(name = "mascot_emotion")      val mascotEmotion     : String
)

// ── Ответ от GET /exercises ───────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class ExerciseDto(
    @Json(name = "id")         val id         : String,
    @Json(name = "phoneme")    val phoneme    : String,
    @Json(name = "mode")       val mode       : String,
    @Json(name = "content")    val content    : String,
    @Json(name = "difficulty") val difficulty : Int
)

// ── Ответ от GET /progress ────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class ProgressResponse(
    @Json(name = "user_id")         val userId        : String,
    @Json(name = "phoneme_scores")  val phonemeScores : Map<String, Float>,
    @Json(name = "total_sessions")  val totalSessions : Int,
    @Json(name = "current_streak")  val currentStreak : Int,
    @Json(name = "longest_streak")  val longestStreak : Int,
    @Json(name = "total_xp")        val totalXp       : Int,
    @Json(name = "level")           val level         : Int
)