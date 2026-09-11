package com.spellit.presentation.screens.kid.gameplay.hard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spellit.presentation.theme.BubblePink
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.PalePink

private val qwertyRows = listOf(
    listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
    listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
    listOf("Z", "X", "C", "V", "B", "N", "M")
)

/**
 * A manual on-screen QWERTY keyboard. No system (or third-party) keyboard is
 * ever shown, so predictive text / autocomplete cannot interfere with the
 * child's spelling.
 */
@Composable
fun QwertyKeyboard(
    input: String,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var upper by remember { mutableStateOf(false) }

    fun charFor(letter: String): String = if (upper) letter else letter.lowercase()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        qwertyRows.forEachIndexed { index, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (index == 2) {
                    ShiftKey(upper = upper, onClick = { upper = !upper })
                }
                row.forEach { letter ->
                    LetterKey(
                        label = charFor(letter),
                        onClick = {
                            if (input.length < 30) {
                                onInputChange(input + charFor(letter))
                            }
                        }
                    )
                }
                if (index == 2) {
                    BackspaceKey(onClick = {
                        if (input.isNotEmpty()) onInputChange(input.dropLast(1))
                    })
                }
            }
        }
    }
}

@Composable
private fun RowScope.LetterKey(label: String, onClick: () -> Unit) {
    KeyButton(modifier = Modifier.weight(1f), onClick = onClick) {
        Text(
            text = label,
            color = DeepBlue,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.ShiftKey(upper: Boolean, onClick: () -> Unit) {
    KeyButton(modifier = Modifier.weight(1.5f), accent = true, onClick = onClick) {
        Text(
            text = "⇧",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp
        )
    }
}

@Composable
private fun RowScope.BackspaceKey(onClick: () -> Unit) {
    KeyButton(modifier = Modifier.weight(1.6f), accent = true, onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Backspace",
            tint = Color.White
        )
    }
}

@Composable
private fun KeyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .background(
                color = if (accent) BubblePink else DeepBlue.copy(alpha = 0.14f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LetterSlots(
    word: String,
    typed: String,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        word.indices.forEach { index ->
            val char = typed.getOrNull(index)?.toString() ?: ""
            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(54.dp)
                    .background(
                        color = if (char.isEmpty()) Color.White else PalePink.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (char.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(3.dp)
                            .align(Alignment.BottomCenter)
                            .background(Color(0xFFBDBDBD), RoundedCornerShape(2.dp))
                    )
                } else {
                    Text(
                        text = char,
                        color = DeepBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    )
                }
            }
        }
    }
}