package com.earnreels.app.domain.model

data class AppSettings(
    val dailyStepGoal: Int = 8000,
    val targetBedtimeHour: Int = 23,
    val targetBedtimeMinute: Int = 0,
    val minSleepDurationMinutes: Int = 420, // 7 hours
    val dailyReelsCap: Int = 30,
    val blockingEnabled: Boolean = true,
    val strictModeAppBlocking: Boolean = false
) {
    val formattedBedtime: String
        get() {
            val period = if (targetBedtimeHour >= 12) "PM" else "AM"
            val displayHour = when {
                targetBedtimeHour == 0 -> 12
                targetBedtimeHour > 12 -> targetBedtimeHour - 12
                else -> targetBedtimeHour
            }
            val displayMinute = String.format("%02d", targetBedtimeMinute)
            return "$displayHour:$displayMinute $period"
        }

    val formattedMinSleep: String
        get() {
            val hours = minSleepDurationMinutes / 60
            val minutes = minSleepDurationMinutes % 60
            return if (minutes == 0) "${hours}h" else "${hours}h ${minutes}m"
        }
}
