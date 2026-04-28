package com.example.soyle.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.data.repository.SettingsRepository
import com.example.soyle.domain.model.UserSettings
import com.example.soyle.ui.theme.AppLanguage
import com.example.soyle.ui.theme.applyTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading : Boolean      = true,
    val settings  : UserSettings = UserSettings(),
    val isSaving  : Boolean      = false,
    val savedOk   : Boolean      = false,
    val error     : String?      = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    init { loadSettings() }

    fun loadSettings() {
        viewModelScope.launch {
            repo.settingsFlow()
                .catch { e -> _state.value = SettingsUiState(isLoading = false, error = e.message) }
                .collect { settings ->
                    _state.value = SettingsUiState(isLoading = false, settings = settings)
                    // Применяем немедленно
                    applyTheme(settings.theme == "dark")
                    AppLanguage.code = settings.language
                }
        }
    }

    fun setTheme(isDark: Boolean) {
        applyTheme(isDark)                                          // мгновенно
        viewModelScope.launch {
            repo.updateTheme(if (isDark) "dark" else "light")      // сохраняем
        }
    }

    fun setLanguage(code: String) {
        AppLanguage.code = code                                     // мгновенно
        viewModelScope.launch { repo.updateLanguage(code) }
    }

    fun saveProfile(displayName: String, avatarEmoji: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, savedOk = false, error = null)
            repo.updateProfile(displayName, avatarEmoji).fold(
                onSuccess = { _state.value = _state.value.copy(isSaving = false, savedOk = true) },
                onFailure = { _state.value = _state.value.copy(isSaving = false, error = it.message) }
            )
        }
    }

    fun saveAboutMe(goal: String, ageGroup: String, notes: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, savedOk = false, error = null)
            repo.updateAboutMe(goal, ageGroup, notes).fold(
                onSuccess = { _state.value = _state.value.copy(isSaving = false, savedOk = true) },
                onFailure = { _state.value = _state.value.copy(isSaving = false, error = it.message) }
            )
        }
    }

    fun clearSavedOk() {
        _state.value = _state.value.copy(savedOk = false)
    }
}
