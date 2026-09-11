package com.spellit.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Spacer

@Composable
fun PinDots(
    pin: String,
    modifier: Modifier = Modifier,
    error: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        repeat(4) { index ->
            val filled = index < pin.length
            Box(
                modifier = Modifier
                    .size(if (filled) 56.dp else 44.dp)
                    .clip(CircleShape)
                    .background(if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun PinPad(
    onDigit: (Char) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9'),
        listOf('0')
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                    PinKey(key = row[0], onDigit = onDigit, modifier = Modifier.weight(1f))
                    PinKey(key = '\u232B', onDigit = null, onBackspace = onBackspace, modifier = Modifier.weight(1f))
                } else {
                    row.forEach { key -> PinKey(key = key, onDigit = onDigit, modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun PinKey(
    key: Char,
    onDigit: ((Char) -> Unit)?,
    modifier: Modifier = Modifier,
    onBackspace: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                if (key == '\u232B') onBackspace?.invoke() else onDigit?.invoke(key)
            },
        contentAlignment = Alignment.Center
    ) {
        if (key == '\u232B') {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = key.toString(),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}