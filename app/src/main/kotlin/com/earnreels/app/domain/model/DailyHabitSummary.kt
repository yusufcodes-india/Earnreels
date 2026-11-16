package com.earnreels.app.domain.model

data class DailyHabitSummary(
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
)
