package com.example.soyle.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.domain.model.Exercise
import com.example.soyle.domain.model.UserProgress
import com.example.soyle.domain.repository.SpeechRepository
import com.example.soyle.domain.usecase.GetDailyExercises
import com.example.soyle.ui.components.Achievement as UiAchievement
import com.example.soyle.ui.components.checkAchievement
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading          : Boolean        = true,
    val userName           : String         = "Ученик",
    val currentStreak      : Int            = 0,
    val longestStreak      : Int            = 0,
    val totalXp            : Int            = 0,
    val level              : Int            = 1,
    val xpToNextLevel      : Int            = 500,
    val xpProgress         : Float          = 0f,
    val exercises          : List<Exercise> = emptyList(),
    val todayDone          : Int            = 0,
    val todayTotal         : Int            = 5,
    val greeting           : String         = "Привет!",
    val avgScore           : Int            = 0,
    val weekCheckedCount   : Int            = 0,
    val unlockedAchievement: UiAchievement? = null,
    val error              : String?        = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyExercises : GetDailyExercises,
    private val repository        : SpeechRepository,
    private val auth              : FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val userId get() = auth.currentUser?.uid ?: ""
    private val userName get() = auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: auth.currentUser?.email?.substringBefore("@") ?: "Ученик"

    init { loadData() }

    private fun loadData() {
        val uid = userId
        if (uid.isEmpty()) {
            _uiState.value = HomeUiState(isLoading = false)
            return
        }

        viewModelScope.launch {
            try {
                val exercises = getDailyExercises(uid)
                _uiState.update { it.copy(exercises = exercises, isLoading = false, userName = userName) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }

            repository.getUserProgress(uid)
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { progress ->
                    _uiState.update { state ->
                        val xpInCurrentLevel = progress.totalXp % 500
                        val avg = if (progress.phonemeScores.isEmpty()) 0
                            else progress.phonemeScores.values.average().toInt()
                        val weekChecked = minOf(progress.currentStreak, 7)
                        // Проверяем новые ачивки
                        val newAchievement = checkAchievement(
                            streak       = progress.currentStreak,
                            totalXp      = progress.totalXp,
                            sessionCount = progress.totalSessions,
                            prevStreak   = state.currentStreak,
                            prevXp       = state.totalXp,
                            prevSessions = state.todayDone
                        )
                        state.copy(
                            currentStreak       = progress.currentStreak,
                            longestStreak       = progress.longestStreak,
                            totalXp             = progress.totalXp,
                            level               = progress.level,
                            xpProgress          = xpInCurrentLevel / 500f,
                            xpToNextLevel       = 500 - xpInCurrentLevel,
                            greeting            = buildGreeting(progress),
                            avgScore            = avg,
                            weekCheckedCount    = weekChecked,
                            todayDone           = progress.totalSessions,
                            unlockedAchievement = newAchievement ?: state.unlockedAchievement
                        )
                    }
                }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadData()
    }

    fun dismissAchievement() {
        _uiState.update { it.copy(unlockedAchievement = null) }
    }

    private fun buildGreeting(progress: UserProgress): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val timeGreeting = when {
            hour < 12 -> "Доброе утро"
            hour < 17 -> "Добрый день"
            else      -> "Добрый вечер"
        }
        return when {
            progress.currentStreak >= 7 -> "$timeGreeting! 🔥 Ты огонь — ${progress.currentStreak} дней подряд!"
            progress.currentStreak >= 3 -> "$timeGreeting! 💪 Уже ${progress.currentStreak} дня подряд!"
            progress.currentStreak == 1 -> "$timeGreeting! Отличное начало! 🌟"
            else                        -> "$timeGreeting! Пора тренироваться! 🦉"
        }
    }
}
