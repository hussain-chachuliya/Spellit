package com.spellit.presentation.screens.kid.gameplay.medium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.spellit.domain.model.WordChunker
import com.spellit.presentation.screens.kid.gameplay.TileDropBoard

/**
 * Medium mode: every letter is jumbled and the child arranges the letter tiles.
 */
@Composable
fun MediumGame(word: String, onSubmit: (Boolean) -> Unit) {
    val letters = remember(word) { WordChunker.scrambleLetters(word).map { it.toString() } }
    TileDropBoard(
        tiles = letters,
        chunkMode = false,
        onComplete = { result -> onSubmit(result.equals(word, ignoreCase = true)) }
    )
}