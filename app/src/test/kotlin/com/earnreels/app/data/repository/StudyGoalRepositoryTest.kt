package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.StudyGoalDao
import com.earnreels.app.data.local.entity.StudyGoalEntity
import com.earnreels.app.domain.model.StudyGoalCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StudyGoalRepositoryTest {

    private lateinit var fakeDao: FakeStudyGoalDao
    private lateinit var repository: StudyGoalRepository

    @Before
    fun setup() {
        fakeDao = FakeStudyGoalDao()
        repository = StudyGoalRepository(fakeDao)
    }

    @Test
    fun addGoal_persistsAndCalculatesRemainingMinutes() = runBlocking {
        val goalId = repository.addGoal(
            title = "Physics Lecture 4",
            targetMinutes = 60,
            category = StudyGoalCategory.LECTURE,
            date = "2026-11-17"
        )

        val retrieved = repository.getGoalById(goalId)
        assertNotNull(retrieved)
        assertEquals("Physics Lecture 4", retrieved?.title)
        assertEquals(60, retrieved?.targetMinutes)
        assertEquals(0, retrieved?.completedMinutes)
        assertEquals(60, retrieved?.remainingMinutes)
        assertEquals(0f, retrieved?.progress ?: 0f, 0.01f)
        assertFalse(retrieved?.isCompleted == true)
    }

    @Test
    fun logProgress_incrementsCompletedMinutesAndUpdatesRemaining() = runBlocking {
        val goalId = repository.addGoal(
            title = "Maths Problem Set",
            targetMinutes = 50,
            category = StudyGoalCategory.STUDY,
            date = "2026-11-17"
        )

        // Complete 25 minutes
        repository.logProgress(goalId, 25)

        val updated = repository.getGoalById(goalId)
        assertNotNull(updated)
        assertEquals(25, updated?.completedMinutes)
        assertEquals(25, updated?.remainingMinutes)
        assertEquals(0.5f, updated?.progress ?: 0f, 0.01f)
        assertFalse(updated?.isCompleted == true)

        // Complete another 25 minutes -> completes goal
        repository.logProgress(goalId, 25)

        val completed = repository.getGoalById(goalId)
        assertNotNull(completed)
        assertEquals(50, completed?.completedMinutes)
        assertEquals(0, completed?.remainingMinutes)
        assertEquals(1.0f, completed?.progress ?: 0f, 0.01f)
        assertTrue(completed?.isCompleted == true)
    }

    @Test
    fun deleteGoal_removesGoal() = runBlocking {
        val goalId = repository.addGoal(
            title = "History Reading",
            targetMinutes = 30,
            category = StudyGoalCategory.LESSON,
            date = "2026-11-17"
        )

        assertNotNull(repository.getGoalById(goalId))
        repository.deleteGoal(goalId)
        assertNull(repository.getGoalById(goalId))
    }

    @Test
    fun getGoalsForDate_returnsGoalsForSpecifiedDate() = runBlocking {
        repository.addGoal("Goal 1", 30, StudyGoalCategory.LECTURE, "2026-11-17")
        repository.addGoal("Goal 2", 45, StudyGoalCategory.REVISION, "2026-11-17")
        repository.addGoal("Goal Other Date", 60, StudyGoalCategory.STUDY, "2026-11-16")

        val goals = repository.getGoalsForDate("2026-11-17").first()
        assertEquals(2, goals.size)
        assertTrue(goals.any { it.title == "Goal 1" })
        assertTrue(goals.any { it.title == "Goal 2" })
    }
}

class FakeStudyGoalDao : StudyGoalDao {
    private val goals = mutableMapOf<Long, StudyGoalEntity>()
    private var idCounter = 1L

    override suspend fun insertGoal(goal: StudyGoalEntity): Long {
        val id = if (goal.id == 0L) idCounter++ else goal.id
        goals[id] = goal.copy(id = id)
        return id
    }

    override suspend fun updateGoal(goal: StudyGoalEntity) {
        goals[goal.id] = goal
    }

    override suspend fun getGoalById(goalId: Long): StudyGoalEntity? {
        return goals[goalId]
    }

    override fun getGoalsForDate(date: String): Flow<List<StudyGoalEntity>> {
        return flowOf(goals.values.filter { it.date == date })
    }

    override suspend fun addCompletedMinutes(goalId: Long, minutes: Int) {
        val existing = goals[goalId] ?: return
        val newCompleted = existing.completedMinutes + minutes
        val isFinished = newCompleted >= existing.targetMinutes
        goals[goalId] = existing.copy(
            completedMinutes = newCompleted,
            isCompleted = isFinished
        )
    }

    override suspend fun deleteGoal(goalId: Long) {
        goals.remove(goalId)
    }

    override suspend fun clearAll() {
        goals.clear()
    }
}
