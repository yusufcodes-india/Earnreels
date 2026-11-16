package com.earnreels.app.domain.usecase

import com.earnreels.app.domain.model.HabitRewardType

/**
 * Pure business logic use case for computing EarnReels reward currency.
 * Reward rules:
 * - 15-min focus: +3 min
 * - 25-min focus: +5 min
 * - 45-min focus: +8 min
 * - 60-min focus: +10 min
 * - Daily steps goal: +10 min
 * - Workout: +15 min
 * - Bedtime routine: +5 min
 * - Minimum sleep duration: +5 min
 * - 3-day combined streak: +10 bonus min
 * - 7-day combined streak: +20 bonus min
 */
class CalculateRewardUseCase {

    fun forFocusSession(durationMinutes: Int): Int {
        return when {
            durationMinutes >= 60 -> 10
            durationMinutes >= 45 -> 8
            durationMinutes >= 25 -> 5
            durationMinutes >= 15 -> 3
            else -> 0
        }
    }

    fun forHabitType(type: HabitRewardType): Int {
        return type.defaultMinutes
    }

    fun forStreakMilestone(days: Int): Int {
        return when (days) {
            3 -> 10
            7 -> 20
            else -> 0
        }
    }

    /**
     * Calculates the actual awarded minutes respecting the remaining cap.
     */
    fun applyCap(requestedMinutes: Int, currentEarnedToday: Int, maxDailyCap: Int = 30): Int {
        val remainingCap = (maxDailyCap - currentEarnedToday).coerceAtLeast(0)
        return minOf(requestedMinutes, remainingCap)
    }
}
