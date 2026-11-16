package com.earnreels.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earnreels.app.data.local.entity.RewardTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: RewardTransactionEntity): Long

    @Query("SELECT * FROM reward_transactions WHERE date = :date ORDER BY timestamp DESC")
    fun getTransactionsForDate(date: String): Flow<List<RewardTransactionEntity>>

    @Query("SELECT * FROM reward_transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 30): Flow<List<RewardTransactionEntity>>

    @Query("SELECT COUNT(*) > 0 FROM reward_transactions WHERE date = :date AND habitType = :habitType")
    suspend fun hasRewardForDate(date: String, habitType: String): Boolean

    @Query("SELECT COALESCE(SUM(amountMinutes), 0) FROM reward_transactions WHERE date = :date")
    fun getTotalEarnedForDate(date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(amountMinutes), 0) FROM reward_transactions WHERE date = :date")
    suspend fun getTotalEarnedForDateSync(date: String): Int
}
