package com.earnreels.app.features.focus

import com.earnreels.app.data.repository.FakeReelsAllowanceDao
import com.earnreels.app.data.repository.FakeRewardTransactionDao
import com.earnreels.app.data.repository.FocusRepository
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.data.repository.StreakRepository
import com.earnreels.app.domain.usecase.CompleteFocusSessionUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FocusTimerCancellationTest {

    private lateinit var fakeFocusDao: FakeFocusSessionDao
    private lateinit var fakeAllowanceDao: FakeReelsAllowanceDao
    private lateinit var fakeTxDao: FakeRewardTransactionDao
    private lateinit var fakeStreakDao: FakeStreakDao
    private lateinit var focusRepo: FocusRepository
    private lateinit var rewardsRepo: RewardsRepository
    private lateinit var streakRepo: StreakRepository
    private lateinit var completeUseCase: CompleteFocusSessionUseCase

    @Before
    fun setup() {
        fakeFocusDao = FakeFocusSessionDao()
        fakeAllowanceDao = FakeReelsAllowanceDao()
        fakeTxDao = FakeRewardTransactionDao()
        fakeStreakDao = FakeStreakDao()

        focusRepo = FocusRepository(fakeFocusDao)
        rewardsRepo = RewardsRepository(fakeAllowanceDao, fakeTxDao)
        streakRepo = StreakRepository(fakeStreakDao, rewardsRepo)
        completeUseCase = CompleteFocusSessionUseCase(focusRepo, rewardsRepo, streakRepo)
    }

    @Test
    fun startingAndCancelingSession_awardsZeroMinutesAndDoesNotIncrementCompletedMinutes() = runBlocking {
        val today = LocalDate.now().toString()
        val sessionId = focusRepo.startSession(25)

        // Session started but canceled before countdown finishes:
        // isCompleted remains false and no rewards are awarded
        val session = fakeFocusDao.getSessionById(sessionId)
        assertFalse(session?.isCompleted == true)
        assertEquals(0, session?.earnedReelsMinutes)

        val totalCompletedMins = focusRepo.getTotalCompletedFocusMinutes(today).first()
        assertEquals(0, totalCompletedMins)

        val allowance = rewardsRepo.getAllowanceForDateSync(today)
        assertEquals(0, allowance.totalEarnedToday)
    }

    @Test
    fun completingGenuineSession_awardsReelsMinutes() = runBlocking {
        val today = LocalDate.now().toString()
        val sessionId = focusRepo.startSession(25)

        val awarded = completeUseCase(sessionId, 25)
        assertEquals(5, awarded)

        val totalCompletedMins = focusRepo.getTotalCompletedFocusMinutes(today).first()
        assertEquals(25, totalCompletedMins)

        val allowance = rewardsRepo.getAllowanceForDateSync(today)
        assertEquals(5, allowance.totalEarnedToday)
    }
}
