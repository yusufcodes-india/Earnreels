package com.earnreels.app.domain.usecase

import java.time.LocalDate

/**
 * Pure streak computation helper to handle consecutive dates, missed days, and same-day logs.
 */
class CalculateStreakUseCase {

    fun computeNextStreak(
        currentStreak: Int,
        lastCompletedDateStr: String?,
        currentDateStr: String
    ): Int {
        if (lastCompletedDateStr == null) return 1

        val lastDate = LocalDate.parse(lastCompletedDateStr)
        val today = LocalDate.parse(currentDateStr)

        return when {
            lastDate.isEqual(today) -> currentStreak // Same day, no streak change
            lastDate.plusDays(1).isEqual(today) -> currentStreak + 1 // Consecutive day
            else -> 1 // Broken streak, restart from 1
        }
    }

    fun isStreakActive(lastCompletedDateStr: String?, currentDateStr: String): Boolean {
        if (lastCompletedDateStr == null) return false
        val lastDate = LocalDate.parse(lastCompletedDateStr)
        val today = LocalDate.parse(currentDateStr)
        return lastDate.isEqual(today) || lastDate.plusDays(1).isEqual(today)
    }
}
