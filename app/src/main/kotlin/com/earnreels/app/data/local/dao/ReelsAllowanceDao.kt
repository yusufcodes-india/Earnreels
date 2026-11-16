package com.earnreels.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earnreels.app.data.local.entity.ReelsAllowanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelsAllowanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllowance(allowance: ReelsAllowanceEntity)

    @Query("SELECT * FROM reels_allowances WHERE date = :date")
    fun getAllowanceForDate(date: String): Flow<ReelsAllowanceEntity?>

    @Query("SELECT * FROM reels_allowances WHERE date = :date")
    suspend fun getAllowanceForDateSync(date: String): ReelsAllowanceEntity?

    @Query("UPDATE reels_allowances SET currentAllowanceMinutes = :remaining WHERE date = :date")
    suspend fun updateRemainingMinutes(date: String, remaining: Int)

    @Query("DELETE FROM reels_allowances")
    suspend fun clearAll()
}
