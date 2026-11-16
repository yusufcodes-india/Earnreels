package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.DailyHabitSummaryDao
import com.earnreels.app.data.local.dao.WorkoutLogDao
import com.earnreels.app.data.local.entity.DailyHabitSummaryEntity
import com.earnreels.app.data.local.entity.WorkoutLogEntity
import com.earnreels.app.domain.model.HabitRewardType
import com.earnreels.app.domain.model.WorkoutLog
import com.earnreels.app.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutRepository(
    private val workoutLogDao: WorkoutLogDao,
    private val summaryDao: DailyHabitSummaryDao,
    private val rewardsRepository: RewardsRepository
) {

    fun getWorkoutForDate(date: String): Flow<WorkoutLog?> {
        return workoutLogDao.getWorkoutForDate(date).map { it?.toDomain() }
    }

    suspend fun hasWorkoutForDate(date: String): Boolean {
        return workoutLogDao.hasWorkoutForDate(date)
    }

    suspend fun logWorkout(date: String, type: WorkoutType): Int {
        if (hasWorkoutForDate(date)) {
            // Already logged workout for today
            return 0
        }

        val awardedMinutes = rewardsRepository.awardReward(
            date = date,
            habitType = HabitRewardType.WORKOUT,
            rewardMinutes = HabitRewardType.WORKOUT.defaultMinutes,
            description = "Completed ${type.displayName} workout",
            allowMultipleSameDay = false
        )

        val logEntity = WorkoutLogEntity(
            date = date,
            workoutType = type.name,
            completedAt = System.currentTimeMillis(),
            earnedMinutes = awardedMinutes
        )
        workoutLogDao.insertWorkout(logEntity)

        // Update daily habit summary
        val summary = summaryDao.getSummaryForDateSync(date) ?: DailyHabitSummaryEntity(date = date)
        summaryDao.upsertSummary(
            summary.copy(
                workoutCompleted = true,
                workoutType = type.displayName
            )
        )

        return awardedMinutes
    }
}
