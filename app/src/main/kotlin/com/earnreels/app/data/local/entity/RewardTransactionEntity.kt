package com.earnreels.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.earnreels.app.domain.model.HabitRewardType
import com.earnreels.app.domain.model.RewardTransaction

@Entity(tableName = "reward_transactions")
data class RewardTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val habitType: String,
    val amountMinutes: Int,
    val timestamp: Long,
    val description: String
) {
    fun toDomain() = RewardTransaction(
        id = id,
        date = date,
        habitType = try {
            HabitRewardType.valueOf(habitType)
        } catch (e: Exception) {
            HabitRewardType.FOCUS_25
        },
        amountMinutes = amountMinutes,
        timestamp = timestamp,
        description = description
    )

    companion object {
        fun fromDomain(model: RewardTransaction) = RewardTransactionEntity(
            id = model.id,
            date = model.date,
            habitType = model.habitType.name,
            amountMinutes = model.amountMinutes,
            timestamp = model.timestamp,
            description = model.description
        )
    }
}
