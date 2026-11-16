package com.earnreels.app.domain.model

data class Streak(
    val habitType: String,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: String? = null
)
