package com.spellit.presentation.screens.kid.modeselect

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.spellit.domain.model.GameMode
import com.spellit.presentation.theme.BubblePink
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.theme.Parchment
import com.spellit.presentation.util.formatCountdown
import com.spellit.presentation.viewmodel.ModeSelectViewModel

@Composable
fun ModeSelectScreen(
    playerName: String,
    onBack: () -> Unit,
    onPick: (GameMode) -> Unit
) {
    val vm: ModeSelectViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
            .safeDrawingPadding()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepBlue, modifier = Modifier.size(28.dp))
            }
            Text(
                text = "Hi $playerName, pick a game!",
                style = MaterialTheme.typography.headlineMedium,
                color = DeepBlue,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        if (!state.loading && !state.canPlay) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "⚠️ Ask a grown-up to add at least 10 words in the admin area first!",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (!state.loading && state.canPlay) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                ModeCard(
                    title = "Easy · Word Blocks",
                    description = "Drag the word blocks into the right order! (Long words, 9+ letters)",
                    icon = Icons.Filled.Sort,
                    color = GrassGreen,
                    enabled = state.easyEnabled,
                    disabledReason = "Need ${ModeSelectViewModel.MIN_WORDS_TO_PLAY} words with 9+ letters (${state.longWordCount} available)",
                    timerSeconds = state.settings.easyTimerSeconds,
                    timerVisible = state.settings.easyTimerVisible,
                    onPick = { onPick(GameMode.EASY) }
                )
                ModeCard(
                    title = "Medium · Letter Mix-Up",
                    description = "Put the scrambled letters back in order!",
                    icon = Icons.Filled.Keyboard,
                    color = SkyBlue,
                    enabled = true,
                    timerSeconds = state.settings.mediumTimerSeconds,
                    timerVisible = state.settings.mediumTimerVisible,
                    onPick = { onPick(GameMode.MEDIUM) }
                )
                ModeCard(
                    title = "Hard · Sound It Out",
                    description = "Listen to the word and type it — no hints!",
                    icon = Icons.Filled.MusicNote,
                    color = BubblePink,
                    enabled = true,
                    timerSeconds = state.settings.hardTimerSeconds,
                    timerVisible = state.settings.hardTimerVisible,
                    onPick = { onPick(GameMode.HARD) }
                )
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    enabled: Boolean,
    onPick: () -> Unit,
    timerSeconds: Int,
    timerVisible: Boolean,
    disabledReason: String = ""
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (enabled) color.copy(alpha = 0.16f) else Color(0xFFF0F0F0),
        onClick = { if (enabled) onPick() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (enabled) color else Color(0xFFBDBDBD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DeepBlue)
                Spacer(Modifier.height(2.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                if (!enabled) {
                    Text("🔒 $disabledReason", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                } else if (timerSeconds > 0) {
                    Text(
                        text = "⏱ ${formatCountdown(timerSeconds * 1000L)} per word${if (!timerVisible) " · timer hidden" else ""}",
                        color = DeepBlue,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}