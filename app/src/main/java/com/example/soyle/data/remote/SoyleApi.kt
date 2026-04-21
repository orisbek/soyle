package com.example.soyle.data.remote

import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class AnalyzeRequest(
    val audio_base64: String,
    val phoneme: String,
    val mode: String
)

@JsonClass(generateAdapter = true)
data class AnalyzeResponse(
    val score: Int,
    val feedback: String,
    val waveform_data: List<Float> = emptyList(),
    val duration_ms: Int = 0
)

interface SoyleApi {
    @POST("/analyze")
    suspend fun analyze(@Body request: AnalyzeRequest): AnalyzeResponse
}
