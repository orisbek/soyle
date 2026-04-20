package com.example.soyle.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.audio.SpeechRecognitionManager
import com.example.soyle.audio.SpeechState
import com.example.soyle.domain.SpeechAnalyzer
import com.example.soyle.domain.model.ExerciseMode
import com.example.soyle.domain.model.MascotEmotion
import com.example.soyle.domain.usecase.SaveAttempt
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
    private val speechRecognitionManager : SpeechRecognitionManager,
    private val speechAnalyzer           : SpeechAnalyzer,
    private val saveAttempt              : SaveAttempt
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun startListening(phoneme: String, mode: ExerciseMode) {
        if (_uiState.value is ExerciseUiState.Recording) return

        viewModelScope.launch {
            _uiState.value = ExerciseUiState.Recording

            speechRecognitionManager.startListening().collect { state ->
                when (state) {
                    is SpeechState.Listening -> { /* уже в состоянии Recording */ }

                    is SpeechState.Success -> {
                        _uiState.value = ExerciseUiState.Analyzing
                        val result = speechAnalyzer.calculatePronunciationScore(phoneme, state.text)
                        val xp = when {
                            result.score >= 85 -> 20
                            result.score >= 65 -> 15
                            result.score >= 45 -> 10
                            else               -> 5
                        }
                        _uiState.value = ExerciseUiState.Success(
                            score    = result.score,
                            feedback = buildFeedback(result.score),
                            emotion  = if (result.isSuccess) MascotEmotion.HAPPY else MascotEmotion.SUPPORTIVE,
                            xp       = xp
                        )
                        saveAttempt("current_user", phoneme, mode, result.score)
                    }

                    is SpeechState.Error -> {
                        _uiState.value = ExerciseUiState.Error(state.message)
                    }

                    else -> {}
                }
            }
        }
    }

    fun reset() { _uiState.value = ExerciseUiState.Idle }

    private fun buildFeedback(score: Int): String = when {
        score >= 85 -> listOf(
            "Верно! Молодец! 🎉",
            "Отлично! Ты настоящий чемпион! 🏆",
            "Великолепно! Так держать! 🌟",
            "Правильно! Умница! 🎊"
        ).random()
        score >= 65 -> listOf(
            "Почти! Ещё чуточку усилий! 😊",
            "Хорошо! Но можно лучше! 💪",
            "Близко! Попробуй ещё раз! 🌈"
        ).random()
        else -> listOf(
            "Старайся! Попробуй ещё раз! 💪",
            "Не сдавайся! Ты можешь лучше! 🦁",
            "Тренируйся! Всё получится! 🌟"
        ).random()
    }
}
