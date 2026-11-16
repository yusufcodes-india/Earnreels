package com.earnreels.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculateStreakUseCaseTest {

    private lateinit var useCase: CalculateStreakUseCase

    @Before
    fun setup() {
        useCase = CalculateStreakUseCase()
    }

    @Test
    fun firstCompletion_startsStreakAtOne() {
        val streak = useCase.computeNextStreak(
            currentStreak = 0,
            lastCompletedDateStr = null,
            currentDateStr = "2026-11-16"
        )
        assertEquals(1, streak)
    }

    @Test
    fun sameDayCompletion_preservesCurrentStreak() {
        val streak = useCase.computeNextStreak(
            currentStreak = 4,
            lastCompletedDateStr = "2026-11-16",
            currentDateStr = "2026-11-16"
        )
        assertEquals(4, streak)
    }

    @Test
    fun consecutiveDay_incrementsStreak() {
        val streak = useCase.computeNextStreak(
            currentStreak = 5,
            lastCompletedDateStr = "2026-11-15",
            currentDateStr = "2026-11-16"
        )
        assertEquals(6, streak)
    }

    @Test
    fun missedDay_resetsStreakToOne() {
        val streak = useCase.computeNextStreak(
            currentStreak = 5,
            lastCompletedDateStr = "2026-11-10",
            currentDateStr = "2026-11-16"
        )
        assertEquals(1, streak)
    }

    @Test
    fun isStreakActive_validatesCorrectly() {
        assertTrue(useCase.isStreakActive("2026-11-16", "2026-11-16")) // Today
        assertTrue(useCase.isStreakActive("2026-11-15", "2026-11-16")) // Yesterday
        assertFalse(useCase.isStreakActive("2026-11-14", "2026-11-16")) // 2 days ago
        assertFalse(useCase.isStreakActive(null, "2026-11-16"))
    }
}
