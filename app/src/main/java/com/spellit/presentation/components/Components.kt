package com.spellit.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spellit.presentation.theme.BubblePink
import com.spellit.presentation.theme.CoralRed
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.GrapePurple
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.theme.SunYellow
import com.spellit.presentation.theme.PaleBlue
import com.spellit.presentation.theme.Ink

@Composable
fun BigRoundButton(
    modifier: Modifier = Modifier,
    color: Color,
    contentPadding: androidx.compose.foundation.layout.PaddingValues =
        androidx.compose.foundation.layout.PaddingValues(24.dp),
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 8.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun KidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (enabled) color else Color(0xFFBDBDBD))
            .clickable(enabled = enabled, indication = null, interactionSource = remember { MutableInteractionSource() }) {
                onClick()
            }
            .padding(horizontal = 28.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SpeakerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = GrapePurple,
    size: Int = 72
) {
    Surface(
        modifier = modifier.size(size.dp),
        color = color,
        shape = CircleShape,
        shadowElevation = 8.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.VolumeUp,
                contentDescription = "Play pronunciation",
                tint = Color.White,
                modifier = Modifier.size((size * 0.55).dp)
            )
        }
    }
}

@Composable
fun RecordingIndicator(
    modifier: Modifier = Modifier,
    blinking: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(if (blinking) CoralRed else Color(0xFFE0E0E0))
        )
        Icon(imageVector = Icons.Filled.GraphicEq, contentDescription = null, tint = CoralRed, modifier = Modifier.size(20.dp))
        Text("Listening...", color = CoralRed, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val bg = if (selected) SkyBlue else PaleBlue
    val fg = if (selected) Color.White else Ink
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(width = 2.dp, color = if (selected) SkyBlue else Color.Transparent, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = fg, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}

@Composable
fun StarRow(
    filledStars: Int,
    modifier: Modifier = Modifier,
    starSize: Int = 48,
    maxStars: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxStars) { index ->
            Star(filled = index < filledStars, size = starSize)
        }
    }
}

@Composable
fun Star(filled: Boolean, size: Int = 48) {
    Text(
        text = if (filled) "\u2605" else "\u2606",
        color = if (filled) SunYellow else Color(0xFFCFC9B8),
        fontSize = size.sp,
        textAlign = TextAlign.Center
    )
}

/** A colorful word tile used by the drag & drop games. */
@Composable
fun WordTile(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = SkyBlue,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}

@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
fun TileImage(icon: ImageVector, contentDescription: String? = null) {
    Icon(imageVector = icon, contentDescription = contentDescription)
}