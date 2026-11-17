package com.earnreels.app.features.focus

import com.earnreels.app.data.local.dao.FocusSessionDao
import com.earnreels.app.data.local.entity.FocusSessionEntity
import com.earnreels.app.data.local.entity.StreakEntity
import com.earnreels.app.data.repository.FakeReelsAllowanceDao
import com.earnreels.app.data.repository.FakeRewardTransactionDao
import com.earnreels.app.data.repository.FakeStudyGoalDao
import com.earnreels.app.data.repository.FocusRepository
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.data.repository.StreakRepository
import com.earnreels.app.data.repository.StudyGoalRepository
import com.earnreels.app.domain.model.StudyGoalCategory
import com.earnreels.app.domain.usecase.CompleteFocusSessionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CompleteFocusSessionUseCaseTest {

    private lateinit var fakeFocusDao: FakeFocusSessionDao
    private lateinit var focusRepo: FocusRepository
    private lateinit var rewardsRepo: RewardsRepository
    private lateinit var streakRepo: StreakRepository
    private lateinit var fakeGoalDao: FakeStudyGoalDao
    private lateinit var goalRepo: StudyGoalRepository
    private lateinit var useCase: CompleteFocusSessionUseCase

    @Before
    fun setup() {
        fakeFocusDao = FakeFocusSessionDao()
        focusRepo = FocusRepository(fakeFocusDao)
        val fakeAllowanceDao = FakeReelsAllowanceDao()
        val fakeTxDao = FakeRewardTransactionDao()
        rewardsRepo = RewardsRepository(fakeAllowanceDao, fakeTxDao)
        val fakeStreakDao = FakeStreakDao()
        streakRepo = StreakRepository(fakeStreakDao, rewardsRepo)
        fakeGoalDao = FakeStudyGoalDao()
        goalRepo = StudyGoalRepository(fakeGoalDao)

        useCase = CompleteFocusSessionUseCase(focusRepo, rewardsRepo, streakRepo, goalRepo)
    }

    @Test
    fun completing25MinSession_awards5MinutesAndUpdatesStreak() = runBlocking {
        val today = LocalDate.now().toString()
        val sessionId = focusRepo.startSession(25)

        val awarded = useCase(sessionId, 25)

        assertEquals(5, awarded)
        val session = fakeFocusDao.getSessionById(sessionId)
        assertTrue(session?.isCompleted == true)
        assertEquals(5, session?.earnedReelsMinutes)

        val streak = streakRepo.getStreakSync(StreakRepository.FOCUS)
        assertEquals(1, streak.currentStreak)

        val allowance = rewardsRepo.getAllowanceForDateSync(today)
        assertEquals(5, allowance.currentAllowanceMinutes)
    }

    @Test
    fun completing60MinSession_awards10Minutes() = runBlocking {
        val sessionId = focusRepo.startSession(60)
        val awarded = useCase(sessionId, 60)
        assertEquals(10, awarded)
    }

    @Test
    fun completingSessionWithLinkedGoal_logsProgressToStudyGoal(): Unit = runBlocking {
        val goalId = goalRepo.addGoal("Chemistry Lecture 3", 50, StudyGoalCategory.LECTURE)
        val sessionId = focusRepo.startSession(25)

        val awarded = useCase(sessionId, 25, linkedGoalId = goalId)

        assertEquals(5, awarded)
        val goal = goalRepo.getGoalById(goalId)
        assertEquals(25, goal?.completedMinutes)
        assertEquals(25, goal?.remainingMinutes)
        assertFalse(goal?.isCompleted == true)
    }
}

class FakeFocusSessionDao : FocusSessionDao {
    private val sessions = mutableMapOf<Long, FocusSessionEntity>()
    private var idCounter = 1L

    override suspend fun insertSession(session: FocusSessionEntity): Long {
        val id = idCounter++
        sessions[id] = session.copy(id = id)
        return id
    }

    override suspend fun updateSession(session: FocusSessionEntity) {
        sessions[session.id] = session
    }

    override suspend fun getSessionById(sessionId: Long): FocusSessionEntity? {
        return sessions[sessionId]
    }

    override fun getSessionsForDate(date: String): Flow<List<FocusSessionEntity>> {
        return flowOf(sessions.values.filter { it.date == date })
    }

    override fun getTotalCompletedFocusMinutes(date: String): Flow<Int> {
        return flowOf(sessions.values.filter { it.date == date && it.isCompleted }.sumOf { it.durationMinutes })
    }

    override suspend fun getCompletedSessionCount(date: String): Int {
        return sessions.values.count { it.date == date && it.isCompleted }
    }
}

class FakeStreakDao : com.earnreels.app.data.local.dao.StreakDao {
    private val map = mutableMapOf<String, StreakEntity>()

    override suspend fun upsertStreak(streak: StreakEntity) {
        map[streak.habitType] = streak
    }

    override fun getStreak(habitType: String): Flow<StreakEntity?> {
        return flowOf(map[habitType])
    }

    override suspend fun getStreakSync(habitType: String): StreakEntity? {
        return map[habitType]
    }

    override fun getAllStreaks(): Flow<List<StreakEntity>> {
        return flowOf(map.values.toList())
    }
}
