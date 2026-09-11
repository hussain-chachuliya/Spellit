package com.spellit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spellit.domain.model.GameMode
import com.spellit.domain.model.GameSession
import com.spellit.domain.usecase.DeleteAllSessionsUseCase
import com.spellit.domain.usecase.DeleteSessionUseCase
import com.spellit.domain.usecase.ObserveSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SessionSort { DATE_NEW, DATE_OLD, MODE, PLAYER }

data class AnalyticsUiState(
    val allSessions: List<GameSession> = emptyList(),
    val modeFilter: GameMode? = null,
    val playerFilter: String? = null,
    val sort: SessionSort = SessionSort.DATE_NEW,
    val players: List<String> = emptyList(),
    val selectedSession: GameSession? = null
) {
    val filteredSessions: List<GameSession> by lazy {
        var list = allSessions
        modeFilter?.let { mode -> list = list.filter { it.gameMode == mode } }
        playerFilter?.let { player -> list = list.filter { it.playerName == player } }
        when (sort) {
            SessionSort.DATE_NEW -> list.sortedByDescending { it.timestamp }
            SessionSort.DATE_OLD -> list.sortedBy { it.timestamp }
            SessionSort.MODE -> list.sortedBy { it.gameMode.name }
            SessionSort.PLAYER -> list.sortedBy { it.playerName.lowercase() }
        }
    }

    val totalSessions: Int get() = allSessions.size
    val totalCorrect: Int get() = allSessions.sumOf { it.correctCount }
    val totalWords: Int get() = allSessions.sumOf { it.totalWords }
    val totalScore: Int get() = allSessions.sumOf { it.score }
}

/**
 * Session history for the admin analytics panel: full filtering, sorting,
 * individual and bulk deletion.
 */
@HiltViewModel
class AdminAnalyticsViewModel @Inject constructor(
    observeSessions: ObserveSessionsUseCase,
    private val deleteSession: DeleteSessionUseCase,
    private val deleteAll: DeleteAllSessionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessions().collect { sessions ->
                val current = _uiState.value
                _uiState.value = current.copy(
                    allSessions = sessions,
                    players = sessions.map { it.playerName }.distinct().sorted()
                )
            }
        }
    }

    fun setModeFilter(mode: GameMode?) {
        _uiState.value = _uiState.value.copy(modeFilter = mode)
    }

    fun setPlayerFilter(player: String?) {
        _uiState.value = _uiState.value.copy(playerFilter = player)
    }

    fun setSort(sort: SessionSort) {
        _uiState.value = _uiState.value.copy(sort = sort)
    }

    fun selectSession(session: GameSession?) {
        _uiState.value = _uiState.value.copy(selectedSession = session)
    }

    fun deleteOne(session: GameSession) {
        viewModelScope.launch {
            deleteSession(session.id)
            if (_uiState.value.selectedSession?.id == session.id) {
                _uiState.value = _uiState.value.copy(selectedSession = null)
            }
        }
    }

    fun deleteAllSessions() {
        viewModelScope.launch {
            deleteAll()
            _uiState.value = _uiState.value.copy(selectedSession = null)
        }
    }
}