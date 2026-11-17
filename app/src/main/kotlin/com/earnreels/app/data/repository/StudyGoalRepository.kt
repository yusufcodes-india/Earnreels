package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.StudyGoalDao
import com.earnreels.app.data.local.entity.StudyGoalEntity
import com.earnreels.app.domain.model.StudyGoal
import com.earnreels.app.domain.model.StudyGoalCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class StudyGoalRepository(private val studyGoalDao: StudyGoalDao) {

    fun getGoalsForDate(date: String): Flow<List<StudyGoal>> {
        return studyGoalDao.getGoalsForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun addGoal(
        title: String,
        targetMinutes: Int,
        category: StudyGoalCategory = StudyGoalCategory.STUDY,
        date: String = LocalDate.now().toString()
    ): Long {
        val entity = StudyGoalEntity(
            title = title.trim(),
            targetMinutes = targetMinutes,
            completedMinutes = 0,
            date = date,
            category = category.name,
            isCompleted = false
        )
        return studyGoalDao.insertGoal(entity)
    }

    suspend fun logProgress(goalId: Long, minutes: Int) {
        studyGoalDao.addCompletedMinutes(goalId, minutes)
    }

    suspend fun getGoalById(goalId: Long): StudyGoal? {
        return studyGoalDao.getGoalById(goalId)?.toDomain()
    }

    suspend fun deleteGoal(goalId: Long) {
        studyGoalDao.deleteGoal(goalId)
    }
}
