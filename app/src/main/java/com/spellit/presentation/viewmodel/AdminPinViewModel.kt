package com.spellit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spellit.domain.usecase.VerifyPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminPinUiState(
    val enteredPin: String = "",
    val error: Boolean = false,
    val unlocked: Boolean = false
)

@HiltViewModel
class AdminPinViewModel @Inject constructor(
    private val verifyPin: VerifyPinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPinUiState())
    val uiState: StateFlow<AdminPinUiState> = _uiState.asStateFlow()

    fun onDigit(digit: Char) {
        val state = _uiState.value
        if (state.enteredPin.length >= 4) return
        val newPin = state.enteredPin + digit
        _uiState.value = state.copy(enteredPin = newPin, error = false)
        if (newPin.length == 4) {
            checkPin(newPin)
        }
    }

    fun onBackspace() {
        val state = _uiState.value
        if (state.enteredPin.isEmpty()) return
        _uiState.value = state.copy(
            enteredPin = state.enteredPin.dropLast(1),
            error = false
        )
    }

    fun clear() {
        _uiState.value = AdminPinUiState()
    }

    private fun checkPin(pin: String) {
        viewModelScope.launch {
            val ok = verifyPin(pin)
            _uiState.value = if (ok) {
                AdminPinUiState(unlocked = true)
            } else {
                AdminPinUiState(enteredPin = "", error = true)
            }
        }
    }
}