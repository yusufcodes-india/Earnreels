package com.earnreels.app.domain.usecase

import com.earnreels.app.domain.model.HabitRewardType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateRewardUseCaseTest {

    private lateinit var useCase: CalculateRewardUseCase

    @Before
    fun setup() {
        useCase = CalculateRewardUseCase()
    }

    @Test
    fun focusSessions_awardCorrectMinutes() {
        assertEquals(3, useCase.forFocusSession(15))
        assertEquals(5, useCase.forFocusSession(25))
        assertEquals(8, useCase.forFocusSession(45))
        assertEquals(10, useCase.forFocusSession(60))
        assertEquals(0, useCase.forFocusSession(10))
    }

    @Test
    fun habits_haveCorrectDefaultRewards() {
        assertEquals(10, useCase.forHabitType(HabitRewardType.STEPS_GOAL))
        assertEquals(15, useCase.forHabitType(HabitRewardType.WORKOUT))
        assertEquals(5, useCase.forHabitType(HabitRewardType.SLEEP_BEDTIME))
        assertEquals(5, useCase.forHabitType(HabitRewardType.SLEEP_DURATION))
    }

    @Test
    fun streakMilestones_awardBonuses() {
        assertEquals(10, useCase.forStreakMilestone(3))
        assertEquals(20, useCase.forStreakMilestone(7))
        assertEquals(0, useCase.forStreakMilestone(4))
    }

    @Test
    fun applyCap_clampsToMaxDailyCap() {
        // When user has 25 min earned, requested 10 min, cap 30 -> awards 5 min
        val awarded1 = useCase.applyCap(requestedMinutes = 10, currentEarnedToday = 25, maxDailyCap = 30)
        assertEquals(5, awarded1)

        // When user has reached 30 min, requested 10 min -> awards 0 min
        val awarded2 = useCase.applyCap(requestedMinutes = 10, currentEarnedToday = 30, maxDailyCap = 30)
        assertEquals(0, awarded2)

        // When user has 0 min, requested 15 min -> awards full 15 min
        val awarded3 = useCase.applyCap(requestedMinutes = 15, currentEarnedToday = 0, maxDailyCap = 30)
        assertEquals(15, awarded3)
    }
}
