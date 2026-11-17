package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.DailyHabitSummaryDao
import com.earnreels.app.data.local.dao.WorkoutLogDao
import com.earnreels.app.data.local.entity.DailyHabitSummaryEntity
import com.earnreels.app.data.local.entity.WorkoutLogEntity
import com.earnreels.app.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DuplicateRewardPreventionTest {

    private lateinit var fakeAllowanceDao: FakeReelsAllowanceDao
    private lateinit var fakeTxDao: FakeRewardTransactionDao
    private lateinit var fakeSummaryDao: FakeDailyHabitSummaryDao
    private lateinit var fakeWorkoutDao: FakeWorkoutLogDao
    private lateinit var rewardsRepo: RewardsRepository
    private lateinit var stepsRepo: StepsRepository
    private lateinit var sleepRepo: SleepRepository
    private lateinit var workoutRepo: WorkoutRepository

    @Before
    fun setup() {
        fakeAllowanceDao = FakeReelsAllowanceDao()
        fakeTxDao = FakeRewardTransactionDao()
        fakeSummaryDao = FakeDailyHabitSummaryDao()
        fakeWorkoutDao = FakeWorkoutLogDao()
        rewardsRepo = RewardsRepository(fakeAllowanceDao, fakeTxDao)
        stepsRepo = StepsRepository(fakeSummaryDao, rewardsRepo)
        sleepRepo = SleepRepository(fakeSummaryDao, rewardsRepo)
        workoutRepo = WorkoutRepository(fakeWorkoutDao, fakeSummaryDao, rewardsRepo)
    }

    @Test
    fun stepGoalReachedMultipleTimesSameDay_awardsOnlyOnce() = runBlocking {
        val today = LocalDate.now().toString()

        // First step update reaches goal
        val firstAward = stepsRepo.updateSteps(today, 8200, 8000)
        assertEquals(10, firstAward)

        // Second step update on same day with even higher steps
        val secondAward = stepsRepo.updateSteps(today, 12000, 8000)
        assertEquals(0, secondAward)

        val allowance = rewardsRepo.getAllowanceForDateSync(today)
        assertEquals(10, allowance.totalEarnedToday)
    }

    @Test
    fun workoutLoggedMultipleTimesSameDay_awardsOnlyOnce() = runBlocking {
        val today = LocalDate.now().toString()

        val firstAward = workoutRepo.logWorkout(today, WorkoutType.GYM)
        assertEquals(15, firstAward)

        // Attempt second workout logging on same day
        val secondAward = workoutRepo.logWorkout(today, WorkoutType.RUNNING)
        assertEquals(0, secondAward)

        val allowance = rewardsRepo.getAllowanceForDateSync(today)
        assertEquals(15, allowance.totalEarnedToday)
    }

    @Test
    fun sleepEvaluatedMultipleTimesSameDay_awardsOnlyOnce() = runBlocking {
        val today = LocalDate.now().toString()
        val zone = java.time.ZoneId.systemDefault()
        val startMillis = LocalDate.now().minusDays(1).atTime(23, 0).atZone(zone).toInstant().toEpochMilli()
        val endMillis = LocalDate.now().atTime(6, 30).atZone(zone).toInstant().toEpochMilli()

        val result1 = sleepRepo.evaluateSleepSession(today, startMillis, endMillis, 23, 0, 420)
        assertEquals(10, result1.totalMinutesAwarded) // 5m bedtime + 5m duration

        // Duplicate evaluation for same date
        val result2 = sleepRepo.evaluateSleepSession(today, startMillis, endMillis, 23, 0, 420)
        assertEquals(0, result2.totalMinutesAwarded)

        val allowance = rewardsRepo.getAllowanceForDateSync(today)
        assertEquals(10, allowance.totalEarnedToday)
    }
}

class FakeDailyHabitSummaryDao : DailyHabitSummaryDao {
    private val summaries = mutableMapOf<String, DailyHabitSummaryEntity>()

    override suspend fun upsertSummary(summary: DailyHabitSummaryEntity) {
        summaries[summary.date] = summary
    }

    override fun getSummaryForDate(date: String): Flow<DailyHabitSummaryEntity?> {
        return flowOf(summaries[date])
    }

    override suspend fun getSummaryForDateSync(date: String): DailyHabitSummaryEntity? {
        return summaries[date]
    }

    override fun getRecentSummaries(limit: Int): Flow<List<DailyHabitSummaryEntity>> {
        return flowOf(summaries.values.sortedByDescending { it.date }.take(limit))
    }

    override suspend fun clearAll() {
        summaries.clear()
    }
}

class FakeWorkoutLogDao : WorkoutLogDao {
    private val logs = mutableMapOf<Long, WorkoutLogEntity>()
    private var idCounter = 1L

    override suspend fun insertWorkout(workout: WorkoutLogEntity): Long {
        val id = idCounter++
        logs[id] = workout.copy(id = id)
        return id
    }

    override fun getWorkoutForDate(date: String): Flow<WorkoutLogEntity?> {
        return flowOf(logs.values.find { it.date == date })
    }

    override suspend fun getWorkoutForDateSync(date: String): WorkoutLogEntity? {
        return logs.values.find { it.date == date }
    }

    override suspend fun hasWorkoutForDate(date: String): Boolean {
        return logs.values.any { it.date == date }
    }

    override fun getRecentWorkouts(limit: Int): Flow<List<WorkoutLogEntity>> {
        return flowOf(logs.values.sortedByDescending { it.completedAt }.take(limit))
    }
}
