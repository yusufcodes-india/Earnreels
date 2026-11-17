package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.StreakDao
import com.earnreels.app.data.local.entity.StreakEntity
import com.earnreels.app.domain.model.HabitRewardType
import com.earnreels.app.domain.model.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class StreakRepository(
    private val streakDao: StreakDao,
    private val rewardsRepository: RewardsRepository
) {

    fun getStreak(habitType: String): Flow<Streak> {
        return streakDao.getStreak(habitType).map {
            val domain = it?.toDomain() ?: Streak(habitType = habitType)
            normalizeActiveStreak(domain)
        }
    }

    suspend fun getStreakSync(habitType: String): Streak {
        val domain = streakDao.getStreakSync(habitType)?.toDomain() ?: Streak(habitType = habitType)
        return normalizeActiveStreak(domain)
    }

    private fun normalizeActiveStreak(streak: Streak): Streak {
        val lastDateStr = streak.lastCompletedDate ?: return streak
        val lastDate = try {
            LocalDate.parse(lastDateStr)
        } catch (e: Exception) {
            return streak
        }
        val today = LocalDate.now()
        // If last completed date was today or yesterday, streak is still active.
        // If last completed date was 2 or more days ago, streak has lapsed (currentStreak = 0).
        val isActive = lastDate.isEqual(today) || lastDate.plusDays(1).isEqual(today)
        return if (isActive) streak else streak.copy(currentStreak = 0)
    }

    /**
     * Updates habit streak for the specified date.
     * If completed on consecutive day, streak increments.
     * If same day, streak unchanged.
     * If gap > 1 day, streak resets to 1.
     */
    suspend fun recordHabitCompletion(habitType: String, completionDate: String): Streak {
        val existing = getStreakSync(habitType)
        val today = LocalDate.parse(completionDate)

        val newStreakCount: Int = if (existing.lastCompletedDate != null) {
            val lastDate = LocalDate.parse(existing.lastCompletedDate)
            when {
                lastDate.isEqual(today) -> existing.currentStreak // Already logged today
                lastDate.plusDays(1).isEqual(today) -> existing.currentStreak + 1
                else -> 1 // Streak broken
            }
        } else {
            1
        }

        val updatedBest = maxOf(newStreakCount, existing.bestStreak)
        val updated = Streak(
            habitType = habitType,
            currentStreak = newStreakCount,
            bestStreak = updatedBest,
            lastCompletedDate = completionDate
        )
        streakDao.upsertStreak(StreakEntity.fromDomain(updated))

        // Check if combined habit streak reached 3-day or 7-day milestones
        if (habitType == COMBINED_HABIT) {
            checkStreakMilestoneRewards(completionDate, newStreakCount)
        }

        return updated
    }

    private suspend fun checkStreakMilestoneRewards(date: String, streakCount: Int) {
        if (streakCount == 3) {
            rewardsRepository.awardReward(
                date = date,
                habitType = HabitRewardType.STREAK_3_DAY,
                rewardMinutes = HabitRewardType.STREAK_3_DAY.defaultMinutes,
                description = "3-day combined habit streak unlocked!",
                allowMultipleSameDay = false
            )
        } else if (streakCount == 7) {
            rewardsRepository.awardReward(
                date = date,
                habitType = HabitRewardType.STREAK_7_DAY,
                rewardMinutes = HabitRewardType.STREAK_7_DAY.defaultMinutes,
                description = "7-day combined habit streak unlocked!",
                allowMultipleSameDay = false
            )
        }
    }

    companion object {
        const val FOCUS = "FOCUS"
        const val STEPS = "STEPS"
        const val WORKOUT = "WORKOUT"
        const val SLEEP = "SLEEP"
        const val COMBINED_HABIT = "COMBINED"
    }
}
