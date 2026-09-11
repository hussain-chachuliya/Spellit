package com.spellit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spellit.domain.model.AppSettings
import com.spellit.domain.model.GameMode
import com.spellit.domain.usecase.GetLongWordCountUseCase
import com.spellit.domain.usecase.GetWordCountUseCase
import com.spellit.domain.usecase.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModeSelectUiState(
    val settings: AppSettings = AppSettings(),
    val totalWords: Int = 0,
    val longWordCount: Int = 0,
    val canPlay: Boolean = false,
    val easyEnabled: Boolean = false,
    val loading: Boolean = true
) {
    fun timerFor(mode: GameMode): Pair<Int, Boolean> = when (mode) {
        GameMode.EASY -> settings.easyTimerSeconds to settings.easyTimerVisible
        GameMode.MEDIUM -> settings.mediumTimerSeconds to settings.mediumTimerVisible
        GameMode.HARD -> settings.hardTimerSeconds to settings.hardTimerVisible
    }
}

@HiltViewModel
class ModeSelectViewModel @Inject constructor(
    getWordCount: GetWordCountUseCase,
    getLongWordCount: GetLongWordCountUseCase,
    observeSettings: ObserveSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModeSelectUiState())
    val uiState: StateFlow<ModeSelectUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val total = getWordCount()
            val long = getLongWordCount()
            observeSettings().collect { settings ->
                _uiState.value = ModeSelectUiState(
                    settings = settings,
                    totalWords = total,
                    longWordCount = long,
                    canPlay = total >= settings.wordsPerSession,
                    easyEnabled = long >= settings.wordsPerSession,
                    loading = false
                )
            }
        }
    }
}