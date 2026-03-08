package com.example.soyle.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SoyleApi {

    /**
     * Основной эндпойнт — анализ произношения.
     * Сервер принимает аудио Base64 + фонему → возвращает score + feedback.
     */
    @POST("analyze")
    suspend fun analyze(
        @Body request: AnalyzeRequest
    ): AnalyzeResponse

    /**
     * Дневные упражнения для пользователя.
     */
    @GET("exercises/{userId}")
    suspend fun getDailyExercises(
        @Path("userId")      userId   : String,
        @Query("phoneme")    phoneme  : String? = null
    ): List<ExerciseDto>

    /**
     * Прогресс пользователя.
     */
    @GET("progress/{userId}")
    suspend fun getProgress(
        @Path("userId") userId : String,
        @Query("days")  days   : Int = 7
    ): ProgressResponse
}