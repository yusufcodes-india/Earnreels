package com.earnreels.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.earnreels.app.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY startedAt DESC")
    fun getSessionsForDate(date: String): Flow<List<FocusSessionEntity>>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM focus_sessions WHERE date = :date AND isCompleted = 1")
    fun getTotalCompletedFocusMinutes(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE date = :date AND isCompleted = 1")
    suspend fun getCompletedSessionCount(date: String): Int
}
