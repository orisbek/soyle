package com.example.soyle.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading  : Boolean = false,
    val isSuccess  : Boolean = false,
    val error      : String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    val isLoggedIn: Boolean get() = authRepository.currentUser != null

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState(isLoading = true)
            authRepository.login(email.trim(), password).fold(
                onSuccess = { _state.value = AuthUiState(isSuccess = true) },
                onFailure = { _state.value = AuthUiState(error = mapFirebaseError(it.message)) }
            )
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _state.value = AuthUiState(isLoading = true)
            authRepository.register(email.trim(), password, name.trim()).fold(
                onSuccess = { _state.value = AuthUiState(isSuccess = true) },
                onFailure = { _state.value = AuthUiState(error = mapFirebaseError(it.message)) }
            )
        }
    }

    fun signOut() = authRepository.signOut()

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun mapFirebaseError(message: String?): String = when {
        message == null -> "Неизвестная ошибка"
        message.contains("email address is already in use") -> "Этот email уже зарегистрирован"
        message.contains("badly formatted") -> "Некорректный email"
        message.contains("password is invalid") || message.contains("no user record") -> "Неверный email или пароль"
        message.contains("INVALID_LOGIN_CREDENTIALS") -> "Неверный email или пароль"
        message.contains("network") || message.contains("Network") -> "Нет соединения с сетью"
        message.contains("weak-password") || message.contains("at least 6") -> "Пароль должен быть не менее 6 символов"
        else -> "Ошибка: $message"
    }
}
