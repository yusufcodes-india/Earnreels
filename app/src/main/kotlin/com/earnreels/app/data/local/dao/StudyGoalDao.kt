package com.earnreels.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.earnreels.app.data.local.entity.StudyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: StudyGoalEntity): Long

    @Update
    suspend fun updateGoal(goal: StudyGoalEntity)

    @Query("SELECT * FROM study_goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Long): StudyGoalEntity?

    @Query("SELECT * FROM study_goals WHERE date = :date ORDER BY isCompleted ASC, id DESC")
    fun getGoalsForDate(date: String): Flow<List<StudyGoalEntity>>

    @Query("UPDATE study_goals SET completedMinutes = completedMinutes + :minutes, isCompleted = CASE WHEN (completedMinutes + :minutes) >= targetMinutes THEN 1 ELSE 0 END WHERE id = :goalId")
    suspend fun addCompletedMinutes(goalId: Long, minutes: Int)

    @Query("DELETE FROM study_goals WHERE id = :goalId")
    suspend fun deleteGoal(goalId: Long)

    @Query("DELETE FROM study_goals")
    suspend fun clearAll()
}
