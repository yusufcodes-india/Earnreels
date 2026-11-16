package com.earnreels.app.domain.model

data class WorkoutLog(
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val workoutType: WorkoutType = WorkoutType.GYM,
    val completedAt: Long = System.currentTimeMillis(),
    val earnedMinutes: Int = 15
)

enum class WorkoutType(val displayName: String) {
    GYM("Gym"),
    RUNNING("Running"),
    HOME("Home workout"),
    YOGA("Yoga"),
    OTHER("Other")
}
