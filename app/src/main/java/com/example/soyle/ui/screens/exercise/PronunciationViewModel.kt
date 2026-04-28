package com.example.soyle.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.audio.SpeechRecognitionManager
import com.example.soyle.audio.SpeechState
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.entity.AttemptEntity
import com.example.soyle.domain.AnalysisResult
import com.example.soyle.domain.SpeechAnalyzer
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PronunciationUiState {
    object Idle : PronunciationUiState()
    object Listening : PronunciationUiState()
    data class Result(
        val expected: String,
        val actual: String,
        val analysis: AnalysisResult,
        val color: Long // Hex color for UI
    ) : PronunciationUiState()
    data class Error(val message: String) : PronunciationUiState()
}

@HiltViewModel
class PronunciationViewModel @Inject constructor(
    private val speechManager : SpeechRecognitionManager,
    private val speechAnalyzer: SpeechAnalyzer,
    private val attemptDao    : AttemptDao,
    private val auth          : FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<PronunciationUiState>(PronunciationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _attemptsCount = MutableStateFlow(0)
    val attemptsCount = _attemptsCount.asStateFlow()

    private val _lastScore = MutableStateFlow<Int?>(null)
    val lastScore = _lastScore.asStateFlow()

    private var recognitionJob: Job? = null

    fun startListening(expectedWord: String) {
        recognitionJob?.cancel()
        recognitionJob = viewModelScope.launch {
            speechManager.startListening().collect { state ->
                when (state) {
                    is SpeechState.Listening -> {
                        _uiState.value = PronunciationUiState.Listening
                    }
                    is SpeechState.Success -> {
                        processResult(expectedWord, state.text)
                    }
                    is SpeechState.Error -> {
                        _uiState.value = PronunciationUiState.Error(state.message)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun processResult(expected: String, actual: String) {
        val analysis = speechAnalyzer.calculatePronunciationScore(expected, actual)
        
        // Цветовая логика по ТЗ
        val color = when {
            analysis.score >= 80 -> 0xFF4CAF50 // Зелёный
            analysis.score >= 50 -> 0xFFFFC107 // Желтый
            else -> 0xFFF44336                // Красный
        }

        _uiState.value = PronunciationUiState.Result(expected, actual, analysis, color)
        _attemptsCount.value += 1
        _lastScore.value = analysis.score

        // Сохраняем в БД для прогресса
        viewModelScope.launch {
            attemptDao.insert(
                AttemptEntity(
                    userId = auth.currentUser?.uid ?: "",
                    phoneme = expected,
                    mode = "SPEECH_PRACTICE",
                    score = analysis.score
                )
            )
        }
    }

    fun reset() {
        _uiState.value = PronunciationUiState.Idle
        recognitionJob?.cancel()
    }
}
