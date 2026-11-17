package com.earnreels.app.domain.usecase

import com.earnreels.app.data.repository.FakeReelsAllowanceDao
import com.earnreels.app.data.repository.FakeRewardTransactionDao
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.domain.model.HabitRewardType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ResetDailyAllowanceUseCaseTest {

    private lateinit var fakeAllowanceDao: FakeReelsAllowanceDao
    private lateinit var fakeTxDao: FakeRewardTransactionDao
    private lateinit var rewardsRepo: RewardsRepository
    private lateinit var resetUseCase: ResetDailyAllowanceUseCase

    @Before
    fun setup() {
        fakeAllowanceDao = FakeReelsAllowanceDao()
        fakeTxDao = FakeRewardTransactionDao()
        rewardsRepo = RewardsRepository(fakeAllowanceDao, fakeTxDao)
        resetUseCase = ResetDailyAllowanceUseCase(rewardsRepo)
    }

    @Test
    fun midnightReset_createsCleanZeroAllowanceForNewDay() = runBlocking {
        // Yesterday: user earned 25 minutes and used 10 minutes (15 min left)
        rewardsRepo.awardReward(
            date = "2026-11-16",
            habitType = HabitRewardType.FOCUS_25,
            rewardMinutes = 25,
            description = "Focus"
        )
        rewardsRepo.deductMinutes("2026-11-16", 10)

        val yesterdayAllowance = rewardsRepo.getAllowanceForDateSync("2026-11-16")
        assertEquals(15, yesterdayAllowance.currentAllowanceMinutes)

        // Midnight arrives -> Reset for new date
        resetUseCase("2026-11-17", maxDailyCap = 30)

        val todayAllowance = rewardsRepo.getAllowanceForDateSync("2026-11-17")
        // Earned allowance expires at midnight and does not carry over
        assertEquals(0, todayAllowance.currentAllowanceMinutes)
        assertEquals(0, todayAllowance.totalEarnedToday)
        assertEquals(0, todayAllowance.totalUsedToday)
        assertEquals(30, todayAllowance.maxDailyCap)
    }

    @Test
    fun newDayWithoutPriorRecords_startsAtZero() = runBlocking {
        val allowance = rewardsRepo.getAllowanceForDateSync("2026-11-18", defaultMaxCap = 30)
        assertEquals(0, allowance.currentAllowanceMinutes)
        assertEquals(0, allowance.totalEarnedToday)
    }
}
