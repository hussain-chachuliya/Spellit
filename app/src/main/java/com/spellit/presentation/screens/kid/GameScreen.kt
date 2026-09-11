package com.spellit.presentation.screens.kid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.spellit.domain.model.GameMode
import com.spellit.presentation.components.BigRoundButton
import com.spellit.presentation.components.SpeakerButton
import com.spellit.presentation.components.StarRow
import com.spellit.presentation.screens.kid.gameplay.easy.EasyGame
import com.spellit.presentation.screens.kid.gameplay.hard.HardGame
import com.spellit.presentation.screens.kid.gameplay.medium.MediumGame
import com.spellit.presentation.screens.kid.results.ResultsScreen
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.Ink
import com.spellit.presentation.theme.PaleBlue
import com.spellit.presentation.theme.PaleGreen
import com.spellit.presentation.theme.Parchment
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.theme.SunYellow
import com.spellit.presentation.util.formatCountdown
import com.spellit.presentation.viewmodel.GamePhase
import com.spellit.presentation.viewmodel.GameUiState
import com.spellit.presentation.viewmodel.GameViewModel
import com.spellit.presentation.viewmodel.WordFeedback

@Composable
fun GameScreen(
    playerName: String,
    onBackToModes: () -> Unit,
    onHome: () -> Unit
) {
    val vm: GameViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (state.phase == GamePhase.LOADING) {
            vm.startGame(playerName)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
    ) {
        when (state.phase) {
            GamePhase.LOADING -> LoadingView()
            GamePhase.PLAYING -> PlayingView(
                state = state,
                onBack = onBackToModes,
                onPlayAudio = vm::playCurrentAudio,
                onSubmit = { correct, kidSpelling -> vm.submitAnswer(correct, kidSpelling) },
                onFeedbackDismissed = vm::onFeedbackDismissed
            )
            GamePhase.FINISHED -> ResultsScreen(
                state = state,
                onPlayAgain = { vm.startGame(playerName) },
                onHome = onHome,
                onModes = onBackToModes
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Getting words ready...", style = MaterialTheme.typography.headlineMedium, color = DeepBlue)
    }
}

@Composable
private fun PlayingView(
    state: GameUiState,
    onBack: () -> Unit,
    onPlayAudio: () -> Unit,
    onSubmit: (Boolean, String) -> Unit,
    onFeedbackDismissed: () -> Unit
) {
    val word = state.currentWord

    Box(Modifier.fillMaxSize()) {
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
                    text = "Word ${state.currentIndex + 1} of ${state.words.size}",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepBlue,
                    modifier = Modifier.weight(1f)
                )
                if (state.timerVisible && state.timerSeconds > 0) {
                    Surface(color = SkyBlue, shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = formatCountdown(state.timeRemainingMillis),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = PaleBlue, shape = RoundedCornerShape(16.dp)) {
                    Text(
                        text = "⭐ ${state.score} points",
                        color = DeepBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
                Surface(color = PaleGreen, shape = RoundedCornerShape(16.dp)) {
                    Text(
                        text = "✔ ${state.correctCount} correct",
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Audio button centered — every mode lets the child replay the pronunciation.
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SpeakerButton(onClick = onPlayAudio, size = 96)
            }

            Spacer(Modifier.height(16.dp))

            if (word != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when (state.mode) {
                        GameMode.EASY -> EasyGame(word.spelling, onSubmit)
                        GameMode.MEDIUM -> MediumGame(word.spelling, onSubmit)
                        GameMode.HARD -> HardGame(word.spelling, onSubmit)
                    }
                }
            } else {
                Text("Hmm, could not load a word.", color = MaterialTheme.colorScheme.error)
            }
        }

        // Feedback overlay
        state.feedback?.let { feedback ->
            FeedbackOverlay(
                feedback = feedback,
                onDone = onFeedbackDismissed
            )
        }
    }
}

@Composable
private fun FeedbackOverlay(
    feedback: WordFeedback,
    onDone: () -> Unit
) {
    val isCorrect = feedback is WordFeedback.Correct
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isCorrect) GrassGreen.copy(alpha = 0.92f) else Color(0xFFE53935).copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isCorrect) {
                Text("🎉", fontSize = 64.sp)
                Text("Correct!", style = MaterialTheme.typography.displaySmall, color = Color.White)
                StarRow(filledStars = 3, starSize = 40)
            } else {
                val wrong = feedback as WordFeedback.Wrong
                Text("❌", fontSize = 64.sp)
                Text("Wrong Spelling", style = MaterialTheme.typography.displaySmall, color = Color.White)
                Spacer(Modifier.height(10.dp))
                if (wrong.kidSpelling.isNotBlank()) {
                    Text(
                        text = "You spelled: ${wrong.kidSpelling}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                }
                Text(
                    text = "It is: ${wrong.word}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(32.dp))
            BigRoundButton(
                onClick = onDone,
                color = if (isCorrect) Color.White else SunYellow,
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Next Word",
                        color = DeepBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = DeepBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}