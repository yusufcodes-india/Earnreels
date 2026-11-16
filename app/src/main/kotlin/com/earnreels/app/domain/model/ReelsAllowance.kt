package com.earnreels.app.domain.model

data class ReelsAllowance(
    val date: String, // YYYY-MM-DD
    val currentAllowanceMinutes: Int = 0,
    val totalEarnedToday: Int = 0,
    val totalUsedToday: Int = 0,
    val maxDailyCap: Int = 30
) {
    val remainingCap: Int
        get() = (maxDailyCap - totalEarnedToday).coerceAtLeast(0)

    val canEarnMore: Boolean
        get() = remainingCap > 0
}
