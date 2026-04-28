package com.example.soyle.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.domain.model.UserProgress
import com.example.soyle.domain.repository.SpeechRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressUiState(
    val isLoading: Boolean       = true,
    val progress : UserProgress? = null,
    val error    : String?       = null
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository : SpeechRepository,
    private val auth       : FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    init { loadProgress() }

    private fun loadProgress() {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = ProgressUiState(isLoading = false, error = "Не авторизован")
            return
        }
        viewModelScope.launch {
            repository.getUserProgress(uid)
                .catch { e -> _uiState.value = _uiState.value.copy(error = e.message, isLoading = false) }
                .collect { data -> _uiState.value = ProgressUiState(isLoading = false, progress = data) }
        }
    }
}
