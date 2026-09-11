package com.spellit.presentation.screens.kid.gameplay.hard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spellit.presentation.components.KidButton
import com.spellit.presentation.theme.BubblePink
import com.spellit.presentation.theme.DeepBlue

/**
 * Hard mode: no hints and no jumbled letters. The child listens to the word
 * (auto-played) and types the spelling on the keyboard.
 */
@Composable
fun HardGame(word: String, onPlayAudio: () -> Unit, onSubmit: (Boolean) -> Unit) {
    key(word) {
        var text by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(word) {
            onPlayAudio()
            focusRequester.requestFocus()
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap the speaker to hear the word, then type it!",
                style = MaterialTheme.typography.titleMedium,
                color = DeepBlue,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Your spelling") },
                placeholder = { Text("Listen 👂 then type...") },
                singleLine = true,
                shape = RoundedCornerShape(22.dp),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    color = DeepBlue,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .focusRequester(focusRequester)
            )
            Spacer(Modifier.height(16.dp))
            val clean = text.trim()
            KidButton(
                text = if (clean.isEmpty()) "Type a word first" else "Check it! ✓",
                onClick = { onSubmit(clean.equals(word, ignoreCase = true)) },
                color = BubblePink,
                enabled = clean.isNotEmpty()
            )
        }
    }
}