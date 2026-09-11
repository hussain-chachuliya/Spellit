package com.spellit.presentation.screens.kid.gameplay

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.spellit.presentation.theme.BrightOrange
import com.spellit.presentation.theme.BubblePink
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.GrapePurple
import com.spellit.presentation.theme.Ink
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.theme.SunYellow

private val palette = listOf(SkyBlue, GrassGreen, BubblePink, SunYellow, GrapePurple, BrightOrange)
private val chunkPalette = listOf(GrassGreen, SkyBlue, BubblePink)

private data class TileToken(val tileIndex: Int, val slotIndex: Int?)

/**
 * Computes the biggest square tile size that keeps every slot and tray tile on
 * screen within [boardHeight]. Easy (chunk) mode forces the 3 blocks onto one
 * row so nothing needs scrolling.
 */
private fun tileSizeFor(chunkMode: Boolean, boardWidth: Dp, boardHeight: Dp, tileCount: Int): Dp {
    val maxSize = 148.dp
    val minSize = 40.dp
    val step = 4.dp
    val spacing = 10.dp
    val layoutWidth = (boardWidth - 16.dp).coerceAtLeast(1.dp)

    fun perRow(size: Dp): Int =
        (((layoutWidth.value + spacing.value) / (size.value + spacing.value)).toInt()).coerceAtLeast(1)

    fun rowsFor(size: Dp): Int = ((tileCount + perRow(size) - 1) / perRow(size)).coerceAtLeast(1)

    fun fits(size: Dp): Boolean {
        // Instruction line + Sps + tray inner padding.
        val verticalFixed = 112.dp
        val slotRows = rowsFor(size)
        val trayRows = rowsFor(size)
        val used = verticalFixed + size * (slotRows + trayRows) + spacing * (slotRows + trayRows - 1)
        return used <= boardHeight
    }

    var size = maxSize
    while (size > minSize) {
        val keepThreeAcross = !chunkMode || perRow(size) >= 3
        if (keepThreeAcross && fits(size)) break
        size -= step
    }
    return size
}

/**
 * A kid-friendly drag-and-drop board. Pieces sit in a tray at the bottom; the
 * child drags or taps them into the slots at the top. When every slot is
 * filled, [onComplete] is invoked with the reconstructed string.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TileDropBoard(
    tiles: List<String>,
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    chunkMode: Boolean = false
) {
    require(tiles.isNotEmpty())

    var slots by remember(tiles) { mutableStateOf(List<Int?>(tiles.size) { null }) }
    var dragging by remember { mutableStateOf<TileToken?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val tileRects = remember { mutableStateMapOf<Int, Rect>() }
    val slotRects = remember { mutableStateMapOf<Int, Rect>() }
    var trayRect by remember { mutableStateOf<Rect?>(null) }

    val colorFor = remember(tiles) {
        tiles.indices.associateWith { (if (chunkMode) chunkPalette else palette)[it % (if (chunkMode) chunkPalette else palette).size] }
    }

    LaunchedEffect(slots) {
        if (slots.all { it != null }) {
            val reconstructed = tiles.indices.joinToString("") { i -> tiles[slots[i]!!] }
            onComplete(reconstructed)
        }
    }

    fun commitSlots(newSlots: List<Int?>) {
        slots = newSlots
    }

    fun tapOnTile(token: TileToken) {
        val newSlots = slots.toMutableList()
        if (token.slotIndex != null) {
            if (newSlots[token.slotIndex] == token.tileIndex) newSlots[token.slotIndex] = null
        } else if (token.tileIndex !in slots.filterNotNull()) {
            val empty = slots.indexOfFirst { it == null }
            if (empty >= 0) newSlots[empty] = token.tileIndex
        }
        commitSlots(newSlots)
    }

    fun drop(token: TileToken) {
        val rect = tileRects[token.tileIndex]
        if (rect != null) {
            val center = Offset(rect.center.x + dragOffset.x, rect.center.y + dragOffset.y)
            val overTray = trayRect?.contains(center) == true
            val targetSlot = slotRects.entries.firstOrNull { it.value.contains(center) }?.key
            val newSlots = slots.toMutableList()
            token.slotIndex?.let { si ->
                if (si in newSlots.indices && newSlots[si] == token.tileIndex) newSlots[si] = null
            }
            when {
                targetSlot != null -> newSlots[targetSlot] = token.tileIndex
                token.slotIndex == null && !overTray -> {
                    // Scanned the empty slots for the neighbour; snap to nearest.
                    val nearest = slotRects.entries
                        .filter { newSlots[it.key] == null }
                        .minByOrNull { (_, r) ->
                            val c = r.center
                            (c.x - center.x) * (c.x - center.x) + (c.y - center.y) * (c.y - center.y)
                        }
                    nearest?.let { (slot, _) -> newSlots[slot] = token.tileIndex }
                }
            }
            commitSlots(newSlots)
        }
        dragging = null
        dragOffset = Offset.Zero
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(4.dp)
    ) {
        val tileSize = tileSizeFor(
            chunkMode = chunkMode,
            boardWidth = maxWidth,
            boardHeight = if (maxHeight == Dp.Infinity) 600.dp else maxHeight,
            tileCount = tiles.size
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = if (chunkMode) "Drag the whole word's 3 blocks into order!" else "Drag the letters into order!",
                style = MaterialTheme.typography.titleLarge,
                color = Ink
            )

            Spacer(Modifier.height(20.dp))

            // Target slots
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                slots.forEachIndexed { slotIndex, tileIndex ->
                    Box(
                        modifier = Modifier.padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { slotRects[slotIndex] = it.boundsInWindow() }
                                .size(tileSize)
                                .border(2.dp, Color(0x6690A4AE), RoundedCornerShape(16.dp))
                                .background(Color(0x22B0BEC5), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (chunkMode && tileIndex == null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .size(28.dp)
                                        .background(Color(0xFF90A4AE).copy(alpha = 0.55f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${slotIndex + 1}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            if (tileIndex != null) {
                                val token = TileToken(tileIndex, slotIndex)
                                val isBeingDragged = dragging == token
                                TilePiece(
                                    token = token,
                                    label = tiles[tileIndex],
                                    color = colorFor[tileIndex] ?: SkyBlue,
                                    chunkMode = chunkMode,
                                    size = tileSize,
                                    isDragging = isBeingDragged,
                                    dragOffset = dragOffset,
                                    onTap = ::tapOnTile,
                                    onDragStart = { dragging = it; dragOffset = Offset.Zero },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        dragOffset += amount
                                    },
                                    onDrop = ::drop,
                                    onTrack = { t, r -> tileRects[t.tileIndex] = r },
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(26.dp))

            // Source tray
            val remaining = tiles.indices.filter { i -> i !in slots.filterNotNull() }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { trayRect = it.boundsInWindow() }
                    .background(Color(0xFFECEFF1), RoundedCornerShape(22.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                remaining.forEach { tileIndex ->
                    val token = TileToken(tileIndex, null)
                    TilePiece(
                        token = token,
                        label = tiles[tileIndex],
                        color = colorFor[tileIndex] ?: SkyBlue,
                        chunkMode = chunkMode,
                        size = tileSize,
                        isDragging = dragging == token,
                        dragOffset = dragOffset,
                        onTap = ::tapOnTile,
                        onDragStart = { dragging = it; dragOffset = Offset.Zero },
                        onDrag = { change, amount ->
                            change.consume()
                            dragOffset += amount
                        },
                        onDrop = ::drop,
                        onTrack = { t, r -> tileRects[t.tileIndex] = r },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun TilePiece(
    token: TileToken,
    label: String,
    color: Color,
    chunkMode: Boolean,
    size: androidx.compose.ui.unit.Dp,
    isDragging: Boolean,
    dragOffset: Offset,
    onTap: (TileToken) -> Unit,
    onDragStart: (TileToken) -> Unit,
    onDrag: (androidx.compose.ui.input.pointer.PointerInputChange, Offset) -> Unit,
    onDrop: (TileToken) -> Unit,
    onTrack: (TileToken, Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .onGloballyPositioned { onTrack(token, it.boundsInWindow()) }
            .pointerInput(token) {
                detectTapGestures { onTap(token) }
            }
            .pointerInput(token) {
                detectDragGestures(
                    onDragStart = { onDragStart(token) },
                    onDrag = { change, amount -> onDrag(change, amount) },
                    onDragEnd = { onDrop(token) },
                    onDragCancel = { onDrop(token) }
                )
            }
            .then(if (isDragging) Modifier.zIndex(10f) else Modifier)
            .graphicsLayer {
                translationX = if (isDragging) dragOffset.x else 0f
                translationY = if (isDragging) dragOffset.y else 0f
            }
            .size(size)
            .background(if (isDragging) color.copy(alpha = 0.85f) else color, RoundedCornerShape(16.dp))
            .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (chunkMode) {
                when {
                    label.length > 15 -> 18.sp
                    label.length > 10 -> 20.sp
                    label.length > 6 -> 24.sp
                    else -> 30.sp
                }
            } else 32.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}