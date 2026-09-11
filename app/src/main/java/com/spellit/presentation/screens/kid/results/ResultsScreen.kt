package com.spellit.presentation.screens.kid.results

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spellit.presentation.components.BigRoundButton
import com.spellit.presentation.components.ConfettiBurst
import com.spellit.presentation.components.StarRow
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.theme.SunYellow
import com.spellit.presentation.theme.Parchment
import com.spellit.presentation.viewmodel.GameUiState

@Composable
fun ResultsScreen(
    state: GameUiState,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    onModes: () -> Unit
) {
    val summary = state.summary
    if (summary == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Finishing up...")
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
    ) {
        ConfettiBurst(modifier = Modifier.fillMaxSize(), play = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Well done, ${state.playerName}!",
                style = MaterialTheme.typography.displaySmall,
                color = DeepBlue,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "You spelled ${summary.correctCount} of ${summary.totalWords} words correctly!",
                style = MaterialTheme.typography.titleLarge,
                color = DeepBlue,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            StarRow(filledStars = summary.stars, starSize = 64)

            Spacer(Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SunYellow.copy(alpha = 0.25f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Text("TOTAL SCORE", style = MaterialTheme.typography.labelLarge, color = DeepBlue)
                Text(
                    text = "${summary.score}",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepBlue
                )
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BigRoundButton(
                    color = GrassGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onPlayAgain
                ) {
                    Text("Play Again", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                BigRoundButton(
                    color = SkyBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onModes
                ) {
                    Text("More Modes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            BigRoundButton(
                color = DeepBlue,
                modifier = Modifier.fillMaxWidth(),
                onClick = onHome
            ) {
                Text("🏠 Back to Home", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}