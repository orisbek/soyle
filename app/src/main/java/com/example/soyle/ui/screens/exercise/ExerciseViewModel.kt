package com.example.soyle.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.audio.AudioRecorder
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.domain.model.MascotEmotion
import com.example.soyle.domain.usecase.AnalyzePronunciation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExerciseUiState {
    data object Idle      : ExerciseUiState()
    data object Recording : ExerciseUiState()
    data object Analyzing : ExerciseUiState()
    data class  Success(
        val score    : Int,
        val feedback : String,
        val emotion  : MascotEmotion,
        val xp       : Int
    ) : ExerciseUiState()
    data class  Error(val message: String) : ExerciseUiState()
}

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val audioRecorder        : AudioRecorder,
    private val analyzePronunciation : AnalyzePronunciation,
    private val auth                 : FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val userId get() = auth.currentUser?.uid ?: ""

    fun startRecording(phoneme: String, mode: ExerciseMode) {
        if (_uiState.value is ExerciseUiState.Recording) return

        viewModelScope.launch {
            try {
                _uiState.value = ExerciseUiState.Recording
                
                // Записываем аудио (3 секунды по умолчанию в AudioRecorder)
                val audioBytes = audioRecorder.record()
                
                _uiState.value = ExerciseUiState.Analyzing

                // Отправляем на анализ (через UseCase, который обращается к репозиторию)
                val result = analyzePronunciation(
                    audioBytes = audioBytes,
                    phoneme    = phoneme,
                    mode       = mode,
                    userId     = userId
                )

                result.onSuccess { pronunciation ->
                    _uiState.value = ExerciseUiState.Success(
                        score    = pronunciation.score,
                        feedback = pronunciation.feedback,
                        emotion  = pronunciation.mascotEmotion,
                        xp       = pronunciation.xpEarned
                    )
                }.onFailure { exception ->
                    _uiState.value = ExerciseUiState.Error(exception.message ?: "Ошибка анализа")
                }

            } catch (e: Exception) {
                _uiState.value = ExerciseUiState.Error(e.message ?: "Ошибка записи")
            }
        }
    }

    fun stopRecording() {
        audioRecorder.stop()
    }

    fun reset() {
        _uiState.value = ExerciseUiState.Idle
    }
}
