package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.ReelsAllowanceDao
import com.earnreels.app.data.local.dao.RewardTransactionDao
import com.earnreels.app.data.local.entity.ReelsAllowanceEntity
import com.earnreels.app.data.local.entity.RewardTransactionEntity
import com.earnreels.app.domain.model.HabitRewardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RewardsRepositoryTest {

    private lateinit var fakeAllowanceDao: FakeReelsAllowanceDao
    private lateinit var fakeTransactionDao: FakeRewardTransactionDao
    private lateinit var repository: RewardsRepository

    @Before
    fun setup() {
        fakeAllowanceDao = FakeReelsAllowanceDao()
        fakeTransactionDao = FakeRewardTransactionDao()
        repository = RewardsRepository(fakeAllowanceDao, fakeTransactionDao)
    }

    @Test
    fun awardReward_awardsMinutesAndPersists() = runBlocking {
        val awarded = repository.awardReward(
            date = "2026-11-16",
            habitType = HabitRewardType.WORKOUT,
            rewardMinutes = 15,
            description = "Gym workout"
        )

        assertEquals(15, awarded)
        val allowance = repository.getAllowanceForDateSync("2026-11-16")
        assertEquals(15, allowance.currentAllowanceMinutes)
        assertEquals(15, allowance.totalEarnedToday)
    }

    @Test
    fun awardReward_preventsDuplicatesOnSameDay() = runBlocking {
        val firstAward = repository.awardReward(
            date = "2026-11-16",
            habitType = HabitRewardType.STEPS_GOAL,
            rewardMinutes = 10,
            description = "Steps reached",
            allowMultipleSameDay = false
        )
        assertEquals(10, firstAward)

        // Attempt second award for same habit on same day
        val secondAward = repository.awardReward(
            date = "2026-11-16",
            habitType = HabitRewardType.STEPS_GOAL,
            rewardMinutes = 10,
            description = "Steps reached again",
            allowMultipleSameDay = false
        )
        assertEquals(0, secondAward)

        val allowance = repository.getAllowanceForDateSync("2026-11-16")
        assertEquals(10, allowance.totalEarnedToday)
    }

    @Test
    fun awardReward_respectsDailyCapOf30Minutes() = runBlocking {
        // Award 25 minutes
        repository.awardReward(
            date = "2026-11-16",
            habitType = HabitRewardType.FOCUS_25,
            rewardMinutes = 25,
            description = "Focus block",
            maxDailyCap = 30,
            allowMultipleSameDay = true
        )

        // Try to award another 15 minutes (should only award remaining 5 min to hit 30 min cap)
        val secondAward = repository.awardReward(
            date = "2026-11-16",
            habitType = HabitRewardType.WORKOUT,
            rewardMinutes = 15,
            description = "Workout",
            maxDailyCap = 30,
            allowMultipleSameDay = true
        )

        assertEquals(5, secondAward)
        val allowance = repository.getAllowanceForDateSync("2026-11-16")
        assertEquals(30, allowance.totalEarnedToday)
        assertEquals(30, allowance.currentAllowanceMinutes)
    }

    @Test
    fun deductMinutes_reducesAllowanceAndClampsAtZero() = runBlocking {
        repository.awardReward(
            date = "2026-11-16",
            habitType = HabitRewardType.FOCUS_25,
            rewardMinutes = 5,
            description = "Focus"
        )

        // Deduct 3 min -> 2 remaining
        val hasRemaining1 = repository.deductMinutes("2026-11-16", 3)
        assertTrue(hasRemaining1)
        val allowance1 = repository.getAllowanceForDateSync("2026-11-16")
        assertEquals(2, allowance1.currentAllowanceMinutes)
        assertEquals(3, allowance1.totalUsedToday)

        // Deduct remaining 2 min -> 0 remaining
        val hasRemaining2 = repository.deductMinutes("2026-11-16", 2)
        assertFalse(hasRemaining2)
        val allowance2 = repository.getAllowanceForDateSync("2026-11-16")
        assertEquals(0, allowance2.currentAllowanceMinutes)
        assertEquals(5, allowance2.totalUsedToday)

        // Deduct when already zero -> stays zero, returns false
        val hasRemaining3 = repository.deductMinutes("2026-11-16", 1)
        assertFalse(hasRemaining3)
    }

    @Test
    fun resetDailyAllowance_resetsAtMidnightWithoutCarryingOver() = runBlocking {
        repository.awardReward(
            date = "2026-11-15",
            habitType = HabitRewardType.FOCUS_25,
            rewardMinutes = 15,
            description = "Focus"
        )

        // New day reset
        repository.resetDailyAllowance("2026-11-16", maxDailyCap = 30)

        val newDayAllowance = repository.getAllowanceForDateSync("2026-11-16")
        assertEquals(0, newDayAllowance.currentAllowanceMinutes)
        assertEquals(0, newDayAllowance.totalEarnedToday)
        assertEquals(0, newDayAllowance.totalUsedToday)
    }
}

// In-memory test doubles
class FakeReelsAllowanceDao : ReelsAllowanceDao {
    private val map = mutableMapOf<String, ReelsAllowanceEntity>()

    override suspend fun upsertAllowance(allowance: ReelsAllowanceEntity) {
        map[allowance.date] = allowance
    }

    override fun getAllowanceForDate(date: String): Flow<ReelsAllowanceEntity?> {
        return flowOf(map[date])
    }

    override suspend fun getAllowanceForDateSync(date: String): ReelsAllowanceEntity? {
        return map[date]
    }

    override suspend fun updateRemainingMinutes(date: String, remaining: Int) {
        map[date]?.let {
            map[date] = it.copy(currentAllowanceMinutes = remaining)
        }
    }

    override suspend fun clearAll() {
        map.clear()
    }
}

class FakeRewardTransactionDao : RewardTransactionDao {
    private val transactions = mutableListOf<RewardTransactionEntity>()

    override suspend fun insertTransaction(transaction: RewardTransactionEntity): Long {
        transactions.add(transaction)
        return transactions.size.toLong()
    }

    override fun getTransactionsForDate(date: String): Flow<List<RewardTransactionEntity>> {
        return flowOf(transactions.filter { it.date == date })
    }

    override fun getRecentTransactions(limit: Int): Flow<List<RewardTransactionEntity>> {
        return flowOf(transactions.take(limit))
    }

    override suspend fun hasRewardForDate(date: String, habitType: String): Boolean {
        return transactions.any { it.date == date && it.habitType == habitType }
    }

    override fun getTotalEarnedForDate(date: String): Flow<Int> {
        return flowOf(transactions.filter { it.date == date }.sumOf { it.amountMinutes })
    }

    override suspend fun getTotalEarnedForDateSync(date: String): Int {
        return transactions.filter { it.date == date }.sumOf { it.amountMinutes }
    }
}
