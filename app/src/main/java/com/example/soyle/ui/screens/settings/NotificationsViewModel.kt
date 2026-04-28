package com.example.soyle.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.data.repository.NotificationTimes
import com.example.soyle.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val morningEnabled : Boolean = true,
    val morningHour    : Int     = 8,
    val morningMinute  : Int     = 0,
    val eveningEnabled : Boolean = true,
    val eveningHour    : Int     = 20,
    val eveningMinute  : Int     = 0,
    val isSaving       : Boolean = false,
    val error          : String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state = _state.asStateFlow()

    init { loadTimes() }

    private fun loadTimes() {
        viewModelScope.launch {
            settingsRepository.getNotificationTimes().onSuccess { times ->
                _state.update {
                    it.copy(
                        morningEnabled = times.morningEnabled,
                        morningHour    = times.morningHour,
                        morningMinute  = times.morningMinute,
                        eveningEnabled = times.eveningEnabled,
                        eveningHour    = times.eveningHour,
                        eveningMinute  = times.eveningMinute
                    )
                }
            }
        }
    }

    fun setMorningEnabled(v: Boolean) = _state.update { it.copy(morningEnabled = v) }
    fun setMorningHour(v: Int)        = _state.update { it.copy(morningHour    = v) }
    fun setMorningMinute(v: Int)      = _state.update { it.copy(morningMinute  = v) }
    fun setEveningEnabled(v: Boolean) = _state.update { it.copy(eveningEnabled = v) }
    fun setEveningHour(v: Int)        = _state.update { it.copy(eveningHour    = v) }
    fun setEveningMinute(v: Int)      = _state.update { it.copy(eveningMinute  = v) }

    fun save(onSuccess: () -> Unit) {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            settingsRepository.updateNotificationTimes(
                NotificationTimes(
                    morningEnabled = s.morningEnabled,
                    morningHour    = s.morningHour,
                    morningMinute  = s.morningMinute,
                    eveningEnabled = s.eveningEnabled,
                    eveningHour    = s.eveningHour,
                    eveningMinute  = s.eveningMinute
                )
            ).onSuccess {
                _state.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
