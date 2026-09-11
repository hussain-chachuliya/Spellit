package com.spellit.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spellit.domain.model.GameSession
import com.spellit.domain.model.ScoreCalculator
import com.spellit.domain.usecase.ObserveSessionsUseCase
import com.spellit.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrophyUiState(
    val sessions: List<GameSession> = emptyList(),
    val selectedSession: GameSession? = null
) {
    val decorationStars: Int get() = sessions.sumOf { ScoreCalculator.starRating(it.correctCount, it.totalWords) }
    val bestScore: Int get() = sessions.maxOfOrNull { it.score } ?: 0
    val bestCorrect: Int get() = sessions.maxOfOrNull { it.correctCount } ?: 0
    val totalWords: Int get() = sessions.sumOf { it.totalWords }
    val totalCorrect: Int get() = sessions.sumOf { it.correctCount }
    val accuracyPercent: Int get() = if (totalWords == 0) 0 else (totalCorrect * 100 / totalWords)
    val gameCount: Int get() = sessions.size
}

/**
 * Kid-facing personal history ("My Trophy Room"). Automatically scoped to the
 * currently selected child name.
 */
@HiltViewModel
class TrophyRoomViewModel @Inject constructor(
    observeSessions: ObserveSessionsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playerName: String = savedStateHandle[Routes.playerArg()] ?: ""

    private val _uiState = MutableStateFlow(TrophyUiState())
    val uiState: StateFlow<TrophyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessions().collect { sessions ->
                val mine = sessions
                    .filter { it.playerName.equals(playerName, ignoreCase = true) }
                    .sortedByDescending { it.timestamp }
                val current = _uiState.value
                _uiState.value = current.copy(
                    sessions = mine,
                    selectedSession = current.selectedSession?.takeIf { sel ->
                        mine.any { it.id == sel.id }
                    }
                )
            }
        }
    }

    fun selectSession(session: GameSession?) {
        _uiState.value = _uiState.value.copy(selectedSession = session)
    }
}