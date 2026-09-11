package com.spellit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spellit.domain.usecase.GetPlayerNamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NameUiState(
    val playerName: String = "",
    val suggestions: List<String> = emptyList()
)

@HiltViewModel
class NameViewModel @Inject constructor(
    private val getPlayerNames: GetPlayerNamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NameUiState())
    val uiState: StateFlow<NameUiState> = _uiState.asStateFlow()

    private val allNames = mutableListOf<String>()

    init {
        viewModelScope.launch {
            allNames.clear()
            allNames.addAll(getPlayerNames())
        }
        refreshSuggestions()
    }

    fun onNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(playerName = value)
        refreshSuggestions()
    }

    fun selectName(name: String) {
        _uiState.value = _uiState.value.copy(playerName = name, suggestions = emptyList())
    }

    private fun refreshSuggestions() {
        val typed = _uiState.value.playerName.trim().lowercase()
        if (typed.isEmpty()) {
            _uiState.value = _uiState.value.copy(suggestions = emptyList())
            return
        }
        val matches = allNames
            .filter { it.lowercase() != typed && it.lowercase().startsWith(typed) }
            .distinct()
            .sorted()
        _uiState.value = _uiState.value.copy(suggestions = matches)
    }
}