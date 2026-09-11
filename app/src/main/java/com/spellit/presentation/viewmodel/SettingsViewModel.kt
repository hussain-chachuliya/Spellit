package com.spellit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spellit.domain.model.AppSettings
import com.spellit.domain.usecase.ObserveSettingsUseCase
import com.spellit.domain.usecase.SaveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val pendingPin: String = "",
    val savedMessage: Boolean = false
)

/**
 * Admin-facing settings: per-mode timers, countdown visibility and the admin PIN.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    private val saveSettings: SaveSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    settings = settings,
                    pendingPin = settings.adminPin
                )
            }
        }
    }

    fun onTimerChange(mode: com.spellit.domain.model.GameMode, seconds: Int) {
        val s = _uiState.value.settings
        val next = when (mode) {
            com.spellit.domain.model.GameMode.EASY -> s.copy(easyTimerSeconds = seconds)
            com.spellit.domain.model.GameMode.MEDIUM -> s.copy(mediumTimerSeconds = seconds)
            com.spellit.domain.model.GameMode.HARD -> s.copy(hardTimerSeconds = seconds)
        }
        _uiState.value = _uiState.value.copy(settings = next, savedMessage = false)
    }

    fun onTimerVisibilityChange(mode: com.spellit.domain.model.GameMode, visible: Boolean) {
        val s = _uiState.value.settings
        val next = when (mode) {
            com.spellit.domain.model.GameMode.EASY -> s.copy(easyTimerVisible = visible)
            com.spellit.domain.model.GameMode.MEDIUM -> s.copy(mediumTimerVisible = visible)
            com.spellit.domain.model.GameMode.HARD -> s.copy(hardTimerVisible = visible)
        }
        _uiState.value = _uiState.value.copy(settings = next, savedMessage = false)
    }

    fun onPendingPinChange(pin: String) {
        val digitsOnly = pin.filter { it.isDigit() }.take(4)
        _uiState.value = _uiState.value.copy(pendingPin = digitsOnly, savedMessage = false)
    }

    fun save() {
        val state = _uiState.value
        val pin = if (state.pendingPin.length == 4) state.pendingPin else state.settings.adminPin
        val updated = state.settings.copy(adminPin = pin)
        viewModelScope.launch {
            saveSettings(updated)
            _uiState.value = _uiState.value.copy(settings = updated, pendingPin = pin, savedMessage = true)
        }
    }
}