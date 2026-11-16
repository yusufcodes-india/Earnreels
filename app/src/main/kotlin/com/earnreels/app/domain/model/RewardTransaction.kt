package com.earnreels.app.domain.model

data class RewardTransaction(
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val habitType: HabitRewardType,
    val amountMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String
)

enum class HabitRewardType(val defaultMinutes: Int, val description: String) {
    FOCUS_15(3, "15-minute focus session"),
    FOCUS_25(5, "25-minute focus session"),
    FOCUS_45(8, "45-minute focus session"),
    FOCUS_60(10, "60-minute focus session"),
    STEPS_GOAL(10, "Daily step goal reached"),
    WORKOUT(15, "Workout completed"),
    SLEEP_BEDTIME(5, "Bedtime routine followed"),
    SLEEP_DURATION(5, "Minimum sleep duration reached"),
    STREAK_3_DAY(10, "3-day combined habit streak"),
    STREAK_7_DAY(20, "7-day combined habit streak");

    companion object {
        fun fromFocusDuration(durationMinutes: Int): HabitRewardType {
            return when {
                durationMinutes <= 15 -> FOCUS_15
                durationMinutes <= 25 -> FOCUS_25
                durationMinutes <= 45 -> FOCUS_45
                else -> FOCUS_60
            }
        }
    }
}
