package com.example.soyle.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.audio.AudioRecorder
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.domain.model.MascotEmotion
import com.example.soyle.domain.usecase.AnalyzePronunciation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────────────────

sealed class ExerciseUiState {
    data object Idle      : ExerciseUiState()  // ждём нажатия
    data object Recording : ExerciseUiState()  // идёт запись
    data object Analyzing : ExerciseUiState()  // ждём сервер
    data class  Success(
        val score    : Int,
        val feedback : String,
        val emotion  : MascotEmotion,
        val xp       : Int
    ) : ExerciseUiState()
    data class  Error(val message: String) : ExerciseUiState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val analyzePronunciation : AnalyzePronunciation,
    private val audioRecorder        : AudioRecorder
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun startRecording(phoneme: String, mode: ExerciseMode) {
        if (_uiState.value is ExerciseUiState.Recording) return

        viewModelScope.launch {
            _uiState.value = ExerciseUiState.Recording

            // Записываем аудио
            val audioBytes = audioRecorder.record()

            _uiState.value = ExerciseUiState.Analyzing

            // Отправляем на сервер
            analyzePronunciation(
                audioBytes = audioBytes,
                phoneme    = phoneme,
                mode       = mode,
                userId     = "current_user"   // TODO: UserSession
            ).fold(
                onSuccess = { result ->
                    _uiState.value = ExerciseUiState.Success(
                        score    = result.score,
                        feedback = result.feedback,
                        emotion  = result.mascotEmotion,
                        xp       = result.xpEarned
                    )
                },
                onFailure = { e ->
                    _uiState.value = ExerciseUiState.Error(
                        message = e.message ?: "Ошибка соединения с сервером"
                    )
                }
            )
        }
    }

    fun stopRecording() = audioRecorder.stop()

    fun reset() { _uiState.value = ExerciseUiState.Idle }
}