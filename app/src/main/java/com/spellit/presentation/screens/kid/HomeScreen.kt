package com.spellit.presentation.screens.kid

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.spellit.presentation.components.BigRoundButton
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.PaleBlue
import com.spellit.presentation.theme.SunYellow
import com.spellit.presentation.theme.Parchment
import com.spellit.presentation.viewmodel.NameViewModel

@Composable
fun HomeScreen(
    onPlay: (String) -> Unit,
    onTrophy: (String) -> Unit,
    onAdmin: () -> Unit
) {
    val vm: NameViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    var adminTaps by remember { mutableStateOf(0) }

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
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Spell It!",
                style = MaterialTheme.typography.displayMedium,
                color = DeepBlue
            )
            Text(
                text = "Welcome, little speller!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            // Name entry with autocomplete
            OutlinedTextField(
                value = state.playerName,
                onValueChange = vm::onNameChanged,
                label = { Text("What's your name?") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )

            if (state.suggestions.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    LazyColumn(state = rememberLazyListState()) {
                        itemsIndexed(state.suggestions) { _, suggestion ->
                            Text(
                                text = "👋 $suggestion",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { vm.selectName(suggestion) }
                                    .padding(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BigRoundButton(
                    color = GrassGreen,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val name = state.playerName.trim().ifEmpty { "Player" }
                        onPlay(name)
                    }
                ) {
                    Text("▶ PLAY", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
                BigRoundButton(
                    color = SunYellow,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val name = state.playerName.trim().ifEmpty { "Player" }
                        onTrophy(name)
                    }
                ) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = "Trophy Room", tint = Color.White, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.height(6.dp))
                    Text("My Trophy Room", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = PaleBlue),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("How to play", fontWeight = FontWeight.Bold, color = DeepBlue)
                    Text("• Tap to play one of the 3 fun modes", color = MaterialTheme.colorScheme.onBackground)
                    Text("• Listen to the word with the 🔊 button", color = MaterialTheme.colorScheme.onBackground)
                    Text("• Spell it to earn points and stars!", color = MaterialTheme.colorScheme.onBackground)
                }
            }

            Spacer(Modifier.weight(1f))

            // Subtle, hidden admin access: 5 quick taps on the quiet corner
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        adminTaps++
                        if (adminTaps >= 5) {
                            adminTaps = 0
                            onAdmin()
                        }
                    }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color(0x33FFFFFF)
                )
            }
        }
    }
}