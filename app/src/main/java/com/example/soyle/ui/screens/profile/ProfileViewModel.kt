package com.example.soyle.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.domain.model.UserProfile
import com.example.soyle.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: SpeechRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val userId = "current_user"

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            repository.getUserProgress(userId)
                .catch { e ->
                    _uiState.value = ProfileUiState(isLoading = false, error = e.message)
                }
                .collect { progress ->
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        profile = UserProfile(
                            uid = progress.userId,
                            name = "Ученик",
                            totalXp = progress.totalXp,
                            level = progress.level,
                            unlockedLevelsCount = progress.phonemeScores.size + 1 // Примерная логика
                        )
                    )
                }
        }
    }

    fun refresh() {
        loadProfile()
    }
}
