package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.FocusSessionDao
import com.earnreels.app.data.local.entity.FocusSessionEntity
import com.earnreels.app.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class FocusRepository(private val focusSessionDao: FocusSessionDao) {

    suspend fun startSession(durationMinutes: Int): Long {
        val today = LocalDate.now().toString()
        val entity = FocusSessionEntity(
            date = today,
            durationMinutes = durationMinutes,
            startedAt = System.currentTimeMillis(),
            isCompleted = false
        )
        return focusSessionDao.insertSession(entity)
    }

    suspend fun completeSession(sessionId: Long, earnedMinutes: Int) {
        val existing = focusSessionDao.getSessionById(sessionId) ?: return
        val updated = existing.copy(
            completedAt = System.currentTimeMillis(),
            isCompleted = true,
            earnedReelsMinutes = earnedMinutes
        )
        focusSessionDao.updateSession(updated)
    }

    fun getSessionsForDate(date: String): Flow<List<FocusSession>> {
        return focusSessionDao.getSessionsForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getTotalCompletedFocusMinutes(date: String): Flow<Int> {
        return focusSessionDao.getTotalCompletedFocusMinutes(date)
    }

    suspend fun getCompletedSessionCount(date: String): Int {
        return focusSessionDao.getCompletedSessionCount(date)
    }
}
