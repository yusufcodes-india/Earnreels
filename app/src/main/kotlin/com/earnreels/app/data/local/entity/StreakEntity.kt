package com.earnreels.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.earnreels.app.domain.model.Streak

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey
    val habitType: String,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: String? = null
) {
    fun toDomain() = Streak(
        habitType = habitType,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        lastCompletedDate = lastCompletedDate
    )

    companion object {
        fun fromDomain(model: Streak) = StreakEntity(
            habitType = model.habitType,
            currentStreak = model.currentStreak,
            bestStreak = model.bestStreak,
            lastCompletedDate = model.lastCompletedDate
        )
    }
}
