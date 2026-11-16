package com.earnreels.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earnreels.app.data.local.entity.DailyHabitSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyHabitSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSummary(summary: DailyHabitSummaryEntity)

    @Query("SELECT * FROM daily_habit_summaries WHERE date = :date")
    fun getSummaryForDate(date: String): Flow<DailyHabitSummaryEntity?>

    @Query("SELECT * FROM daily_habit_summaries WHERE date = :date")
    suspend fun getSummaryForDateSync(date: String): DailyHabitSummaryEntity?

    @Query("SELECT * FROM daily_habit_summaries ORDER BY date DESC LIMIT :limit")
    fun getRecentSummaries(limit: Int = 7): Flow<List<DailyHabitSummaryEntity>>

    @Query("DELETE FROM daily_habit_summaries")
    suspend fun clearAll()
}
