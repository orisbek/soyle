package com.example.soyle.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthFormState(
    val email: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.observeAuthState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    private val _uiState = MutableStateFlow(AuthFormState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.tryEmit(_uiState.value.copy(email = value, error = null))
    fun onPasswordChange(value: String) = _uiState.tryEmit(_uiState.value.copy(password = value, error = null))
    fun toggleMode() = _uiState.tryEmit(_uiState.value.copy(isRegisterMode = !_uiState.value.isRegisterMode, error = null))

    fun submit() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.length < 6) {
            _uiState.tryEmit(state.copy(error = "Введите корректный email и пароль не короче 6 символов"))
            return
        }

        viewModelScope.launch {
            _uiState.emit(state.copy(isLoading = true, error = null))
            val result = if (state.isRegisterMode) {
                authRepository.register(state.email.trim(), state.password)
            } else {
                authRepository.login(state.email.trim(), state.password)
            }

            _uiState.emit(
                _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.localizedMessage
                )
            )
        }
    }
}
