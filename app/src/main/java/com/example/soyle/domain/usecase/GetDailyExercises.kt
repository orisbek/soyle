package com.example.soyle.domain.usecase

import com.example.soyle.domain.model.Exercise
import com.example.soyle.domain.repository.SpeechRepository
import javax.inject.Inject

class GetDailyExercises @Inject constructor(
    private val repository: SpeechRepository
) {
    suspend operator fun invoke(userId: String): List<Exercise> =
        repository.getDailyExercises(userId)
}