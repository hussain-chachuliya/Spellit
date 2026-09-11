package com.spellit.presentation.screens.kid.gameplay.easy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.spellit.domain.model.WordChunker
import com.spellit.presentation.screens.kid.gameplay.TileDropBoard

/**
 * Easy mode: the whole word is split into 3 big blocks and the child drags
 * them into order to rebuild it. No letter tiles are shown.
 */
@Composable
fun EasyGame(word: String, onSubmit: (Boolean) -> Unit) {
    val chunks = remember(word) { WordChunker.chunkIntoThree(word) }
    val scrambled = remember(word) { chunks.shuffled() }
    TileDropBoard(
        tiles = scrambled,
        chunkMode = true,
        onComplete = { result -> onSubmit(result.replace(" ", "").equals(word, ignoreCase = true)) }
    )
}