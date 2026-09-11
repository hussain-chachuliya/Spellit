package com.spellit.domain

import com.spellit.domain.model.WordChunker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordChunkerTest {

    @Test
    fun chunkIntoThreeReconstructsWord() {
        listOf("authoritative", "butterfly", "international", "extraordinary", "holiday", "mountain", "celebration", "communication", "spellit", "fantastic").
            forEach { word ->
            val chunks = WordChunker.chunkIntoThree(word)
            assertEquals("chunked word must reconstruct: $word chunks=$chunks", word, chunks.joinToString(""))
            assertEquals("must always be 3 chunks for $word", 3, chunks.size)
            assertTrue("every chunk non-empty for $word chunks=$chunks", chunks.all { it.isNotEmpty() })
        }
    }

    @Test
    fun scrambleLettersProducesDifferentOrder() {
        val word = "butterfly"
        val scrambled = WordChunker.scrambleLetters(word)
        assertEquals(word.length, scrambled.size)
        assertEquals(word.toList().sorted(), scrambled.sorted())
        // Scramble should differ from the original arrangement.
        assertNotEquals(word.toList(), scrambled)
    }

    @Test
    fun scrambleForRepeatedCharsStillShuffles() {
        val word = "aabbcc"
        val scrambled = WordChunker.scrambleLetters(word)
        assertEquals(word.toList().sorted(), scrambled.sorted())
    }
}