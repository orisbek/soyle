package com.example.soyle.ui.screens.profile

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

data class ProfileUiState(
    val isLoading : Boolean       = true,
    val userName  : String        = "Ученик",
    val progress  : UserProgress? = null,
    val error     : String?       = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository : SpeechRepository,
    private val auth       : FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init { loadProfile() }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.value = ProfileUiState(isLoading = false, error = "Не авторизован")
            return
        }

        // Имя: displayName из Auth, или email до @, или «Ученик»
        val userName = auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: auth.currentUser?.email?.substringBefore("@")
            ?: "Ученик"

        viewModelScope.launch {
            repository.getUserProgress(uid)
                .catch { e ->
                    _uiState.value = ProfileUiState(isLoading = false, error = e.message)
                }
                .collect { progress ->
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        userName  = userName,
                        progress  = progress
                    )
                }
        }
    }

    fun refresh() = loadProfile()
}
