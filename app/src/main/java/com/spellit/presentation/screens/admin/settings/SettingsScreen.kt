package com.spellit.presentation.screens.admin.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.spellit.domain.model.GameMode
import com.spellit.presentation.components.KidButton
import com.spellit.presentation.theme.BubblePink
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.PaleBlue
import com.spellit.presentation.theme.PaleGreen
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen() {
    val vm: SettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.spellit.presentation.theme.Parchment)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Game Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = DeepBlue
        )

        Spacer(Modifier.height(14.dp))

        Surface(shape = RoundedCornerShape(20.dp), color = PaleGreen) {
            Column(Modifier.padding(16.dp)) {
                Text("Words Per Session", fontWeight = FontWeight.Bold, color = DeepBlue, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.settings.wordsPerSession.toString(),
                    onValueChange = { input ->
                        val value = input.filter { it.isDigit() }.toIntOrNull() ?: 10
                        vm.onWordsPerSessionChange(value)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
                Text(
                    text = "How many words per game session (1–50). Default is 10.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepBlue
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        ModeTimerRow(
            mode = GameMode.EASY,
            seconds = state.settings.easyTimerSeconds,
            visible = state.settings.easyTimerVisible,
            color = GrassGreen,
            onSeconds = vm::onTimerChange,
            onVisible = vm::onTimerVisibilityChange
        )

        ModeTimerRow(
            mode = GameMode.MEDIUM,
            seconds = state.settings.mediumTimerSeconds,
            visible = state.settings.mediumTimerVisible,
            color = SkyBlue,
            onSeconds = vm::onTimerChange,
            onVisible = vm::onTimerVisibilityChange
        )

        ModeTimerRow(
            mode = GameMode.HARD,
            seconds = state.settings.hardTimerSeconds,
            visible = state.settings.hardTimerVisible,
            color = BubblePink,
            onSeconds = vm::onTimerChange,
            onVisible = vm::onTimerVisibilityChange
        )

        Spacer(Modifier.height(20.dp))

        Surface(shape = RoundedCornerShape(20.dp), color = PaleBlue) {
            Column(Modifier.padding(16.dp)) {
                Text("Change Admin PIN (4 digits)", fontWeight = FontWeight.Bold, color = DeepBlue, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.pendingPin,
                    onValueChange = vm::onPendingPinChange,
                    label = { Text("New PIN") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Default PIN is 1234. Use only digits.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepBlue
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        KidButton(
            text = "💾 Save Settings",
            onClick = vm::save,
            color = GrassGreen,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.savedMessage) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Saved! ✔",
                color = GrassGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ModeTimerRow(
    mode: GameMode,
    seconds: Int,
    visible: Boolean,
    color: Color,
    onSeconds: (GameMode, Int) -> Unit,
    onVisible: (GameMode, Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.14f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = "${mode.displayName} Mode Timer",
                fontWeight = FontWeight.ExtraBold,
                color = DeepBlue,
                fontSize = 18.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Show countdown to the child", style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
                }
                Switch(
                    checked = visible,
                    onCheckedChange = { onVisible(mode, it) },
                    colors = SwitchDefaults.colors(checkedTrackColor = color)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text("Timer per word (seconds):", style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
            OutlinedTextField(
                value = seconds.toString(),
                onValueChange = { input ->
                    val value = input.filter { it.isDigit() }.take(3).toIntOrNull() ?: 0
                    onSeconds(mode, value.coerceIn(0, 300))
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
            Text(
                text = if (seconds <= 0) "0 = no timer (1 point per correct word)"
                else "Timer active — bonus points for speed",
                style = MaterialTheme.typography.bodyMedium,
                color = DeepBlue
            )
        }
    }
}