package com.example.soyle.domain.usecase

import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.domain.repository.SpeechRepository
import javax.inject.Inject

class SaveAttempt @Inject constructor(
    private val repository: SpeechRepository
) {
    suspend operator fun invoke(
        userId  : String,
        phoneme : String,
        mode    : ExerciseMode,
        score   : Int
    ) = repository.saveAttempt(userId, phoneme, mode, score)
}