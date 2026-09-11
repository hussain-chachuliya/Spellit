package com.spellit.presentation.screens.kid.trophy

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.spellit.domain.model.GameSession
import com.spellit.domain.model.ScoreCalculator
import com.spellit.presentation.components.StarRow
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.PaleBlue
import com.spellit.presentation.theme.PaleGreen
import com.spellit.presentation.theme.PaleYellow
import com.spellit.presentation.theme.Parchment
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.theme.SunYellow
import com.spellit.presentation.util.FormatUtil
import com.spellit.presentation.viewmodel.TrophyRoomViewModel

@Composable
fun TrophyRoomScreen(
    playerName: String,
    onBack: () -> Unit
) {
    val vm: TrophyRoomViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepBlue, modifier = Modifier.size(28.dp))
                }
                Text(
                    text = "My Trophy Room",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DeepBlue,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = SunYellow, modifier = Modifier.size(36.dp))
            }

            if (state.sessions.isEmpty()) {
                EmptyTrophy()
            } else {
                TrophySummary(state = state, playerName = playerName)

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.sessions, key = { it.id }) { session ->
                        SessionRow(
                            session = session,
                            onClick = { vm.selectSession(session) }
                        )
                    }
                }
            }
        }

        state.selectedSession?.let { session ->
            SessionDetailOverlay(
                session = session,
                onDismiss = { vm.selectSession(null) }
            )
        }
    }
}

@Composable
private fun EmptyTrophy() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🏆", fontSize = 80.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No games yet!",
            style = MaterialTheme.typography.headlineMedium,
            color = DeepBlue
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Play a game and your stars will show up here!",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TrophySummary(state: com.spellit.presentation.viewmodel.TrophyUiState, playerName: String) {
    val trophyStars = (state.accuracyPercent / 20).coerceIn(0, 5)
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = PaleYellow
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$playerName's stats",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepBlue
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBlock(value = "${state.gameCount}", label = "Games")
                StatBlock(value = "${state.totalCorrect}/${state.totalWords}", label = "Words right")
                StatBlock(value = "${state.bestScore}", label = "Best score")
                StatBlock(value = "${state.accuracyPercent}%", label = "Accuracy")
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "\uD83C\uDFC6 " + "★".repeat(trophyStars) + "☆".repeat(5 - trophyStars),
                color = DeepBlue,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatBlock(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DeepBlue)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun SessionRow(session: GameSession, onClick: () -> Unit) {
    val stars = ScoreCalculator.starRating(session.correctCount, session.totalWords)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (stars >= 3) PaleGreen else PaleBlue,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "${session.gameMode.displayName} game",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = DeepBlue
                )
                Text(
                    text = "${FormatUtil.formatDate(session.timestamp)} at ${FormatUtil.formatTime(session.timestamp)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${session.score} points · ${session.correctCount}/${session.totalWords} correct",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            StarRow(filledStars = stars, starSize = 26)
        }
    }
}

@Composable
private fun SessionDetailOverlay(session: GameSession, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                )
        ) {
            Column(Modifier.padding(20.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${session.gameMode.displayName} · ${session.score} points",
                        style = MaterialTheme.typography.headlineSmall,
                        color = DeepBlue
                    )
                    StarRow(
                        filledStars = ScoreCalculator.starRating(session.correctCount, session.totalWords),
                        starSize = 34
                    )
                    Text(
                        text = "${FormatUtil.formatDate(session.timestamp)} · ${FormatUtil.formatTime(session.timestamp)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.height(14.dp))
                session.results.forEachIndexed { index, result ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}.",
                            fontWeight = FontWeight.Bold,
                            color = DeepBlue
                        )
                        Text(
                            text = result.spelling,
                            fontWeight = FontWeight.SemiBold,
                            color = if (result.correct) GrassGreen else MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (result.correct) "✔ Correct" else "✘ Wrong",
                            color = if (result.correct) GrassGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = SkyBlue,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Back",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 52.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}