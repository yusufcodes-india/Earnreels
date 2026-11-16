package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.ReelsAllowanceDao
import com.earnreels.app.data.local.dao.RewardTransactionDao
import com.earnreels.app.data.local.entity.ReelsAllowanceEntity
import com.earnreels.app.data.local.entity.RewardTransactionEntity
import com.earnreels.app.domain.model.HabitRewardType
import com.earnreels.app.domain.model.ReelsAllowance
import com.earnreels.app.domain.model.RewardTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RewardsRepository(
    private val reelsAllowanceDao: ReelsAllowanceDao,
    private val rewardTransactionDao: RewardTransactionDao
) {

    fun getAllowanceForDate(date: String): Flow<ReelsAllowance> {
        return reelsAllowanceDao.getAllowanceForDate(date).map { entity ->
            entity?.toDomain() ?: ReelsAllowance(date = date, maxDailyCap = 30)
        }
    }

    suspend fun getAllowanceForDateSync(date: String, defaultMaxCap: Int = 30): ReelsAllowance {
        val existing = reelsAllowanceDao.getAllowanceForDateSync(date)
        return existing?.toDomain() ?: ReelsAllowance(date = date, maxDailyCap = defaultMaxCap)
    }

    /**
     * Awards Reels minutes for a completed habit.
     * Prevents duplicate rewards for single-award habits on the same day.
     * Respects the maximum daily cap.
     * Returns the actual minutes awarded (may be less than requested if hitting the cap).
     */
    suspend fun awardReward(
        date: String,
        habitType: HabitRewardType,
        rewardMinutes: Int,
        description: String,
        maxDailyCap: Int = 30,
        allowMultipleSameDay: Boolean = false
    ): Int {
        if (!allowMultipleSameDay && rewardTransactionDao.hasRewardForDate(date, habitType.name)) {
            // Already awarded today
            return 0
        }

        val currentAllowance = getAllowanceForDateSync(date, maxDailyCap)
        val remainingCap = (maxDailyCap - currentAllowance.totalEarnedToday).coerceAtLeast(0)
        val actualMinutesToAward = minOf(rewardMinutes, remainingCap)

        if (actualMinutesToAward > 0) {
            val updatedAllowance = currentAllowance.copy(
                currentAllowanceMinutes = currentAllowance.currentAllowanceMinutes + actualMinutesToAward,
                totalEarnedToday = currentAllowance.totalEarnedToday + actualMinutesToAward,
                maxDailyCap = maxDailyCap
            )
            reelsAllowanceDao.upsertAllowance(ReelsAllowanceEntity.fromDomain(updatedAllowance))

            val transaction = RewardTransaction(
                date = date,
                habitType = habitType,
                amountMinutes = actualMinutesToAward,
                timestamp = System.currentTimeMillis(),
                description = description
            )
            rewardTransactionDao.insertTransaction(RewardTransactionEntity.fromDomain(transaction))
        }

        return actualMinutesToAward
    }

    /**
     * Deducts minutes while the user is watching Reels/Shorts.
     * Clamps remaining allowance to 0.
     * Returns true if allowance remains (> 0), false if allowance has reached zero.
     */
    suspend fun deductMinutes(date: String, minutesToDeduct: Int = 1): Boolean {
        val currentAllowance = getAllowanceForDateSync(date)
        val newBalance = (currentAllowance.currentAllowanceMinutes - minutesToDeduct).coerceAtLeast(0)
        val newTotalUsed = currentAllowance.totalUsedToday + minOf(minutesToDeduct, currentAllowance.currentAllowanceMinutes)

        val updated = currentAllowance.copy(
            currentAllowanceMinutes = newBalance,
            totalUsedToday = newTotalUsed
        )
        reelsAllowanceDao.upsertAllowance(ReelsAllowanceEntity.fromDomain(updated))
        return newBalance > 0
    }

    /**
     * Resets the allowance at midnight.
     * Earned minutes expire at midnight and do not carry over.
     */
    suspend fun resetDailyAllowance(newDate: String, maxDailyCap: Int = 30) {
        val newAllowance = ReelsAllowance(
            date = newDate,
            currentAllowanceMinutes = 0,
            totalEarnedToday = 0,
            totalUsedToday = 0,
            maxDailyCap = maxDailyCap
        )
        reelsAllowanceDao.upsertAllowance(ReelsAllowanceEntity.fromDomain(newAllowance))
    }

    suspend fun hasRewardForDate(date: String, habitType: HabitRewardType): Boolean {
        return rewardTransactionDao.hasRewardForDate(date, habitType.name)
    }

    fun getTransactionsForDate(date: String): Flow<List<RewardTransaction>> {
        return rewardTransactionDao.getTransactionsForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getTotalEarnedForDate(date: String): Flow<Int> {
        return rewardTransactionDao.getTotalEarnedForDate(date)
    }
}
