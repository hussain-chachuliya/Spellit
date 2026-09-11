package com.spellit.presentation.screens.admin.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.spellit.presentation.components.PinDots
import com.spellit.presentation.components.PinPad
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.Parchment
import com.spellit.presentation.viewmodel.AdminPinViewModel

@Composable
fun AdminPinScreen(
    onBack: () -> Unit,
    onUnlocked: () -> Unit
) {
    val vm: AdminPinViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.unlocked) {
        if (state.unlocked) {
            onUnlocked()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepBlue)
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "🔐 Admin PIN",
                style = MaterialTheme.typography.headlineLarge,
                color = DeepBlue
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Enter the 4-digit code",
                style = MaterialTheme.typography.bodyLarge,
                color = DeepBlue,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            PinDots(pin = state.enteredPin, error = state.error)
            if (state.error) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "That code wasn't right. Try again!",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(36.dp))
            PinPad(onDigit = vm::onDigit, onBackspace = vm::onBackspace)
        }
    }
}