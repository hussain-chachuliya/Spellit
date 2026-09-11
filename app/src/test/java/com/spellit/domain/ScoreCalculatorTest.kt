package com.spellit.domain

import com.spellit.domain.model.ScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreCalculatorTest {

    @Test
    fun noTimerGivesOnePointPerCorrectWord() {
        assertEquals(1, ScoreCalculator.pointsForWord(true, null, null))
        assertEquals(1, ScoreCalculator.pointsForWord(true, remainingTimeMillis = null, totalTimeMillis = 30_000L))
    }

    @Test
    fun wrongWordGivesZeroPoints() {
        assertEquals(0, ScoreCalculator.pointsForWord(false, null, null))
        assertEquals(0, ScoreCalculator.pointsForWord(false, 20_000L, 30_000L))
    }

    @Test
    fun timerBonusScalesWithRemainingTime() {
        val fast = ScoreCalculator.pointsForWord(true, 29_000L, 30_000L)
        val slow = ScoreCalculator.pointsForWord(true, 1_000L, 30_000L)
        assertTrue("faster answer must score >= slower answer", fast >= slow)
        assertTrue("fast answer should be > 1 point", fast > 1)
        assertEquals(fast, 5)
        assertEquals(slow, 1)
    }

    @Test
    fun starRatingVariesCorrectly() {
        assertEquals(5, ScoreCalculator.starRating(10, 10))
        assertEquals(4, ScoreCalculator.starRating(8, 10))
        assertEquals(3, ScoreCalculator.starRating(6, 10))
        assertEquals(2, ScoreCalculator.starRating(4, 10))
        assertEquals(1, ScoreCalculator.starRating(1, 10))
        assertEquals(0, ScoreCalculator.starRating(0, 10))
        assertEquals(0, ScoreCalculator.starRating(0, 0))
    }
}