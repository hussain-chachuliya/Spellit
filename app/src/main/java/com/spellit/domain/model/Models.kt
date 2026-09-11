package com.spellit.domain.model

import kotlin.math.roundToInt

enum class GameMode(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

data class Word(
    val id: Long,
    val spelling: String,
    val audioFileName: String?
) {
    val isLongWord: Boolean get() = spelling.length >= GameConfig.EASY_MIN_LENGTH
}

data class WordResult(
    val wordId: Long,
    val spelling: String,
    val correct: Boolean,
    val remainingTimeMillis: Long? = null,
    val answeredAtMillis: Long = System.currentTimeMillis()
) {
    fun points(totalTimeMillis: Long? = null): Int = ScoreCalculator.pointsForWord(correct, remainingTimeMillis, totalTimeMillis)
}

data class GameSession(
    val id: Long = 0L,
    val playerName: String,
    val gameMode: GameMode,
    val timestamp: Long,
    val results: List<WordResult>,
    val score: Int
) {
    val correctCount: Int get() = results.count { it.correct }
    val totalWords: Int get() = results.size
}

data class AppSettings(
    val id: Int = 0,
    val adminPin: String = "1234",
    val wordsPerSession: Int = 10,
    val easyTimerSeconds: Int = 0,
    val mediumTimerSeconds: Int = 0,
    val hardTimerSeconds: Int = 0,
    val easyTimerVisible: Boolean = true,
    val mediumTimerVisible: Boolean = true,
    val hardTimerVisible: Boolean = true
)

object GameConfig {
    const val WORDS_PER_ROUND = 10
    const val MIN_TOTAL_WORDS = 10
    const val EASY_MIN_LENGTH = 9
    const val EASY_MIN_LONG_WORDS = 10
}

/**
 * Pure, unit-testable gameplay logic.
 */
object ScoreCalculator {
    /**
     * 1 point per correct word when no timer is configured.
     * When a timer is active, points scale from 1..5 based on remaining time.
     */
    fun pointsForWord(
        correct: Boolean,
        remainingTimeMillis: Long?,
        totalTimeMillis: Long?
    ): Int {
        if (!correct) return 0
        if (remainingTimeMillis == null || totalTimeMillis == null || totalTimeMillis <= 0L) return 1
        val ratio = remainingTimeMillis.coerceIn(0L, totalTimeMillis).toDouble() / totalTimeMillis
        return (1 + (ratio * 4.0)).roundToInt().coerceIn(1, 5)
    }

    /**
     * 0..5 stars based on the percentage of correct words.
     */
    fun starRating(correctCount: Int, wordCount: Int): Int {
        if (wordCount <= 0) return 0
        val pct = correctCount.toFloat() / wordCount
        return when {
            pct >= 0.98f -> 5
            pct >= 0.8f -> 4
            pct >= 0.6f -> 3
            pct >= 0.4f -> 2
            pct > 0f -> 1
            else -> 0
        }
    }
}

/**
 * Splits a word into 3 kid-friendly chunks, preferring syllable-like (vowel)
 * boundaries near the one-third and two-third positions.
 */
object WordChunker {
    private val vowels = setOf('a', 'e', 'i', 'o', 'u', 'y')

    fun chunkIntoThree(word: String): List<String> {
        val n = word.length
        if (n < 3) return listOf(word, "", "")
        if (n == 3) return listOf(word.substring(0, 1), word.substring(1, 2), word.substring(2))
        if (n == 4) return listOf(word.substring(0, 1), word.substring(1, 3), word.substring(3))
        val idealOne = (n / 3.0).toInt()
        val idealTwo = (2 * n / 3.0).toInt()
        val cutOne = findCut(word, idealOne, 1, n - 2)
        val cutTwo = findCut(word, idealTwo, cutOne + 1, n - 1)
        return listOf(
            word.substring(0, cutOne),
            word.substring(cutOne, cutTwo),
            word.substring(cutTwo)
        )
    }

    private fun findCut(word: String, ideal: Int, minCut: Int, maxCut: Int): Int {
        val n = word.length
        val window = 2
        var best = ideal.coerceIn(minCut, maxCut)
        var bestScore = Int.MAX_VALUE
        for (candidate in ((ideal - window).coerceAtLeast(minCut)..(ideal + window).coerceAtMost(maxCut))) {
            val left = word[candidate - 1].lowercaseChar() in vowels
            val right = word[candidate].lowercaseChar() in vowels
            val score = when {
                left && !right -> 0    // vowel-consonant boundary is best
                !left && right -> 1
                else -> 2
            }
            if (score < bestScore) {
                bestScore = score
                best = candidate
            }
        }
        return best
    }

    fun scrambleLetters(word: String): List<Char> {
        val letters = word.toMutableList()
        var shuffled = letters.shuffled()
        var attempts = 0
        while (shuffled == letters && attempts < 10) {
            shuffled = letters.shuffled()
            attempts++
        }
        // Ensure the scramble is actually different from the original.
        return if (shuffled == letters) {
            val swapped = letters.toMutableList()
            if (swapped.size >= 2) {
                val i = swapped.indices.lastOrNull { j -> swapped[j] != letters[j] }
                if (i != null && i > 0) {
                    val tmp = swapped[i]
                    swapped[i] = swapped[i - 1]
                    swapped[i - 1] = tmp
                }
            }
            swapped
        } else shuffled
    }
}