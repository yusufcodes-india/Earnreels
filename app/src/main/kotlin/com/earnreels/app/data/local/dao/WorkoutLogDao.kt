package com.earnreels.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earnreels.app.data.local.entity.WorkoutLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutLogEntity): Long

    @Query("SELECT * FROM workout_logs WHERE date = :date LIMIT 1")
    fun getWorkoutForDate(date: String): Flow<WorkoutLogEntity?>

    @Query("SELECT * FROM workout_logs WHERE date = :date LIMIT 1")
    suspend fun getWorkoutForDateSync(date: String): WorkoutLogEntity?

    @Query("SELECT COUNT(*) > 0 FROM workout_logs WHERE date = :date")
    suspend fun hasWorkoutForDate(date: String): Boolean

    @Query("SELECT * FROM workout_logs ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentWorkouts(limit: Int = 7): Flow<List<WorkoutLogEntity>>
}
