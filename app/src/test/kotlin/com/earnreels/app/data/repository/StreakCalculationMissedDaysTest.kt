package com.earnreels.app.data.repository

import com.earnreels.app.features.focus.FakeStreakDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class StreakCalculationMissedDaysTest {

    private lateinit var fakeStreakDao: FakeStreakDao
    private lateinit var fakeAllowanceDao: FakeReelsAllowanceDao
    private lateinit var fakeTxDao: FakeRewardTransactionDao
    private lateinit var rewardsRepo: RewardsRepository
    private lateinit var streakRepo: StreakRepository

    @Before
    fun setup() {
        fakeStreakDao = FakeStreakDao()
        fakeAllowanceDao = FakeReelsAllowanceDao()
        fakeTxDao = FakeRewardTransactionDao()
        rewardsRepo = RewardsRepository(fakeAllowanceDao, fakeTxDao)
        streakRepo = StreakRepository(fakeStreakDao, rewardsRepo)
    }

    @Test
    fun streak_incrementsOnConsecutiveDays() = runBlocking {
        val yesterday = LocalDate.now().minusDays(1).toString()
        val today = LocalDate.now().toString()

        streakRepo.recordHabitCompletion(StreakRepository.FOCUS, yesterday)
        val streak1 = streakRepo.getStreakSync(StreakRepository.FOCUS)
        assertEquals(1, streak1.currentStreak)

        streakRepo.recordHabitCompletion(StreakRepository.FOCUS, today)
        val streak2 = streakRepo.getStreakSync(StreakRepository.FOCUS)
        assertEquals(2, streak2.currentStreak)
        assertEquals(2, streak2.bestStreak)
    }

    @Test
    fun streak_remainsUnchangedOnSameDayCompletion() = runBlocking {
        val today = LocalDate.now().toString()

        streakRepo.recordHabitCompletion(StreakRepository.FOCUS, today)
        streakRepo.recordHabitCompletion(StreakRepository.FOCUS, today)

        val streak = streakRepo.getStreakSync(StreakRepository.FOCUS)
        assertEquals(1, streak.currentStreak)
    }

    @Test
    fun streak_resetsToOneAfterGapGreaterThanOneDay() = runBlocking {
        val pastDate = LocalDate.now().minusDays(5).toString()
        val today = LocalDate.now().toString()

        val record1 = streakRepo.recordHabitCompletion(StreakRepository.FOCUS, pastDate)
        assertEquals(1, record1.currentStreak)

        // Resumes today after 5 days gap -> streak resets to 1
        val record2 = streakRepo.recordHabitCompletion(StreakRepository.FOCUS, today)
        assertEquals(1, record2.currentStreak)
    }

    @Test
    fun readingStreak_returnsZeroIfLastCompletedDateWasTwoOrMoreDaysAgo() = runBlocking {
        // Logged 3 days in past
        val pastDate = LocalDate.now().minusDays(3).toString()
        streakRepo.recordHabitCompletion(StreakRepository.FOCUS, pastDate)

        // Read streak today without completing any habit
        val activeStreak = streakRepo.getStreakSync(StreakRepository.FOCUS)
        assertEquals(0, activeStreak.currentStreak)
        assertEquals(1, activeStreak.bestStreak)
    }
}
