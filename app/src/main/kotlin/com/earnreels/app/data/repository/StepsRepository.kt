package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.DailyHabitSummaryDao
import com.earnreels.app.data.local.entity.DailyHabitSummaryEntity
import com.earnreels.app.domain.model.DailyHabitSummary
import com.earnreels.app.domain.model.HabitRewardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StepsRepository(
    private val summaryDao: DailyHabitSummaryDao,
    private val rewardsRepository: RewardsRepository
) {

    fun getSummaryForDate(date: String): Flow<DailyHabitSummary?> {
        return summaryDao.getSummaryForDate(date).map { it?.toDomain() }
    }

    suspend fun updateSteps(date: String, steps: Int, goal: Int = 8000): Int {
        val current = summaryDao.getSummaryForDateSync(date) ?: DailyHabitSummaryEntity(
            date = date,
            stepGoal = goal
        )

        var awardedMinutes = 0
        var isGoalMet = current.stepRewardAwarded

        // Check if goal met and reward not yet awarded
        if (steps >= goal && !current.stepRewardAwarded) {
            awardedMinutes = rewardsRepository.awardReward(
                date = date,
                habitType = HabitRewardType.STEPS_GOAL,
                rewardMinutes = HabitRewardType.STEPS_GOAL.defaultMinutes,
                description = "Daily step goal reached ($steps / $goal steps)",
                allowMultipleSameDay = false
            )
            isGoalMet = true
        }

        val updated = current.copy(
            stepsCompleted = steps,
            stepGoal = goal,
            stepRewardAwarded = isGoalMet
        )
        summaryDao.upsertSummary(updated)
        return awardedMinutes
    }
}
