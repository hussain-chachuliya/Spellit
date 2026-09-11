package com.spellit.presentation.screens.kid.gameplay.hard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spellit.presentation.components.KidButton
import com.spellit.presentation.theme.BubblePink
import com.spellit.presentation.theme.DeepBlue

/**
 * Hard mode: no hints and no jumbled letters. The child listens to the word
 * (auto-played) and types the spelling on a manual on-screen keyboard so that
 * third-party keyboards (and their autocomplete) never appear.
 */
@Composable
fun HardGame(word: String, onSubmit: (Boolean, String) -> Unit) {
    key(word) {
        var text by remember { mutableStateOf("") }
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
            Spacer(Modifier.height(10.dp))
            LetterSlots(word = word, typed = text)
            Spacer(Modifier.height(12.dp))
            QwertyKeyboard(input = text, onInputChange = { text = it }, modifier = Modifier.padding(horizontal = 4.dp))
            Spacer(Modifier.height(16.dp))
            val clean = text.trim()
            KidButton(
                text = if (clean.isEmpty()) "Type a word first" else "Check it! ✓",
                onClick = { onSubmit(clean.equals(word, ignoreCase = true), clean) },
                color = BubblePink,
                enabled = clean.isNotEmpty()
            )
            Text(
                text = "${clean.length}/${word.length} letters typed",
                color = DeepBlue.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}