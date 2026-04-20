package com.example.soyle.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.domain.model.Exercise
import com.example.soyle.domain.model.UserProgress
import com.example.soyle.domain.usecase.GetDailyExercises
import com.example.soyle.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading     : Boolean        = false,
    val userName      : String         = "Малыш",
    val currentStreak : Int            = 0,
    val longestStreak : Int            = 0,
    val totalXp       : Int            = 0,
    val level         : Int            = 1,
    val xpToNextLevel : Int            = 500,
    val xpProgress    : Float          = 0f,
    val exercises     : List<Exercise> = emptyList(),
    val todayDone     : Int            = 0,
    val todayTotal    : Int            = 5,
    val greeting      : String         = "Привет! Пора тренироваться! 🦉",
    val error         : String?        = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyExercises : GetDailyExercises,
    private val repository        : SpeechRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val userId = "current_user"

    init {
        loadExercises()
        observeProgress()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            try {
                val exercises = getDailyExercises(userId)
                _uiState.update { it.copy(exercises = exercises) }
            } catch (e: Exception) {
                // Показываем пустой список, но не крашим приложение
                _uiState.update { it.copy(exercises = emptyList()) }
            }
        }
    }

    private fun observeProgress() {
        viewModelScope.launch {
            try {
                repository.getUserProgress(userId)
                    .catch { /* ошибка DB — игнорируем, показываем дефолт */ }
                    .collect { progress ->
                        val xpInLevel = progress.totalXp % 500
                        _uiState.update { state ->
                            state.copy(
                                currentStreak = progress.currentStreak,
                                longestStreak = progress.longestStreak,
                                totalXp       = progress.totalXp,
                                level         = progress.level,
                                xpProgress    = xpInLevel / 500f,
                                xpToNextLevel = 500 - xpInLevel,
                                greeting      = buildGreeting(progress)
                            )
                        }
                    }
            } catch (e: Exception) {
                // Ошибка — просто оставляем дефолтные значения
            }
        }
    }

    fun refresh() = loadExercises()

    private fun buildGreeting(progress: UserProgress): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val time = when {
            hour < 12 -> "Доброе утро"
            hour < 17 -> "Добрый день"
            else      -> "Добрый вечер"
        }
        return when {
            progress.currentStreak >= 7 -> "$time! 🔥 Ты огонь — ${progress.currentStreak} дней подряд!"
            progress.currentStreak >= 3 -> "$time! 💪 Уже ${progress.currentStreak} дня подряд!"
            progress.currentStreak == 1 -> "$time! Отличное начало! 🌟"
            else                        -> "$time! Пора тренироваться! 🦉"
        }
    }
}
