package com.earnreels.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.earnreels.app.domain.model.ReelsAllowance

@Entity(tableName = "reels_allowances")
data class ReelsAllowanceEntity(
    @PrimaryKey
    val date: String, // YYYY-MM-DD
    val currentAllowanceMinutes: Int = 0,
    val totalEarnedToday: Int = 0,
    val totalUsedToday: Int = 0,
    val maxDailyCap: Int = 30
) {
    fun toDomain() = ReelsAllowance(
        date = date,
        currentAllowanceMinutes = currentAllowanceMinutes,
        totalEarnedToday = totalEarnedToday,
        totalUsedToday = totalUsedToday,
        maxDailyCap = maxDailyCap
    )

    companion object {
        fun fromDomain(model: ReelsAllowance) = ReelsAllowanceEntity(
            date = model.date,
            currentAllowanceMinutes = model.currentAllowanceMinutes,
            totalEarnedToday = model.totalEarnedToday,
            totalUsedToday = model.totalUsedToday,
            maxDailyCap = model.maxDailyCap
        )
    }
}
