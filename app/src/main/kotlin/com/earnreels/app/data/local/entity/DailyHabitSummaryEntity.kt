package com.earnreels.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.earnreels.app.domain.model.DailyHabitSummary

@Entity(tableName = "daily_habit_summaries")
data class DailyHabitSummaryEntity(
    @PrimaryKey
    val date: String, // YYYY-MM-DD
    val focusMinutesCompleted: Int = 0,
    val stepsCompleted: Int = 0,
    val stepGoal: Int = 8000,
    val stepRewardAwarded: Boolean = false,
    val workoutCompleted: Boolean = false,
    val workoutType: String? = null,
    val sleepBedtimeOnTime: Boolean = false,
    val sleepDurationMinutes: Int = 0,
    val sleepGoalMet: Boolean = false,
    val reelsMinutesEarned: Int = 0,
    val reelsMinutesUsed: Int = 0
) {
    fun toDomain() = DailyHabitSummary(
        date = date,
        focusMinutesCompleted = focusMinutesCompleted,
        stepsCompleted = stepsCompleted,
        stepGoal = stepGoal,
        stepRewardAwarded = stepRewardAwarded,
        workoutCompleted = workoutCompleted,
        workoutType = workoutType,
        sleepBedtimeOnTime = sleepBedtimeOnTime,
        sleepDurationMinutes = sleepDurationMinutes,
        sleepGoalMet = sleepGoalMet,
        reelsMinutesEarned = reelsMinutesEarned,
        reelsMinutesUsed = reelsMinutesUsed
    )

    companion object {
        fun fromDomain(model: DailyHabitSummary) = DailyHabitSummaryEntity(
            date = model.date,
            focusMinutesCompleted = model.focusMinutesCompleted,
            stepsCompleted = model.stepsCompleted,
            stepGoal = model.stepGoal,
            stepRewardAwarded = model.stepRewardAwarded,
            workoutCompleted = model.workoutCompleted,
            workoutType = model.workoutType,
            sleepBedtimeOnTime = model.sleepBedtimeOnTime,
            sleepDurationMinutes = model.sleepDurationMinutes,
            sleepGoalMet = model.sleepGoalMet,
            reelsMinutesEarned = model.reelsMinutesEarned,
            reelsMinutesUsed = model.reelsMinutesUsed
        )
    }
}
