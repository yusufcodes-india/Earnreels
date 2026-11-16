package com.earnreels.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earnreels.app.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStreak(streak: StreakEntity)

    @Query("SELECT * FROM streaks WHERE habitType = :habitType")
    fun getStreak(habitType: String): Flow<StreakEntity?>

    @Query("SELECT * FROM streaks WHERE habitType = :habitType")
    suspend fun getStreakSync(habitType: String): StreakEntity?

    @Query("SELECT * FROM streaks")
    fun getAllStreaks(): Flow<List<StreakEntity>>
}
