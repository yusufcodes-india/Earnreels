package com.earnreels.app.data.repository

import com.earnreels.app.data.local.dao.DailyHabitSummaryDao
import com.earnreels.app.data.local.entity.DailyHabitSummaryEntity
import com.earnreels.app.domain.model.DailyHabitSummary
import com.earnreels.app.domain.model.HabitRewardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class SleepRepository(
    private val summaryDao: DailyHabitSummaryDao,
    private val rewardsRepository: RewardsRepository
) {

    fun getSleepSummaryForDate(date: String): Flow<DailyHabitSummary?> {
        return summaryDao.getSummaryForDate(date).map { it?.toDomain() }
    }

    /**
     * Evaluates sleep session against user bedtime and duration goals.
     * Bedtime window is targetBedtime +/- 30 minutes.
     * Duration meets goal if total duration in minutes >= minDurationMinutes.
     */
    suspend fun evaluateSleepSession(
        date: String,
        startTimeEpochMillis: Long,
        endTimeEpochMillis: Long,
        targetBedtimeHour: Int = 23,
        targetBedtimeMinute: Int = 0,
        minDurationMinutes: Int = 420 // 7 hours
    ): SleepEvaluationResult {
        val zone = ZoneId.systemDefault()
        val startDateTime = Instant.ofEpochMilli(startTimeEpochMillis).atZone(zone)
        val endDateTime = Instant.ofEpochMilli(endTimeEpochMillis).atZone(zone)

        val durationMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime).toInt().coerceAtLeast(0)

        // Evaluate bedtime window (+/- 30 mins from target)
        val actualBedtime = startDateTime.toLocalTime()
        val targetBedtime = LocalTime.of(targetBedtimeHour, targetBedtimeMinute)
        val diffMinutes = abs(ChronoUnit.MINUTES.between(targetBedtime, actualBedtime))
        // Account for day wrap (e.g., 23:30 vs 00:15)
        val normalizedDiffMinutes = minOf(diffMinutes, 1440 - diffMinutes)
        val isOnTime = normalizedDiffMinutes <= 30

        val isDurationMet = durationMinutes >= minDurationMinutes

        var bedtimeAwarded = 0
        if (isOnTime) {
            bedtimeAwarded = rewardsRepository.awardReward(
                date = date,
                habitType = HabitRewardType.SLEEP_BEDTIME,
                rewardMinutes = HabitRewardType.SLEEP_BEDTIME.defaultMinutes,
                description = "Bedtime routine followed (within 30m of goal)",
                allowMultipleSameDay = false
            )
        }

        var durationAwarded = 0
        if (isDurationMet) {
            durationAwarded = rewardsRepository.awardReward(
                date = date,
                habitType = HabitRewardType.SLEEP_DURATION,
                rewardMinutes = HabitRewardType.SLEEP_DURATION.defaultMinutes,
                description = "Minimum sleep duration reached ($durationMinutes m)",
                allowMultipleSameDay = false
            )
        }

        // Update daily habit summary
        val summary = summaryDao.getSummaryForDateSync(date) ?: DailyHabitSummaryEntity(date = date)
        summaryDao.upsertSummary(
            summary.copy(
                sleepBedtimeOnTime = isOnTime,
                sleepDurationMinutes = durationMinutes,
                sleepGoalMet = isDurationMet
            )
        )

        return SleepEvaluationResult(
            durationMinutes = durationMinutes,
            bedtimeOnTime = isOnTime,
            durationMet = isDurationMet,
            totalMinutesAwarded = bedtimeAwarded + durationAwarded
        )
    }
}

data class SleepEvaluationResult(
    val durationMinutes: Int,
    val bedtimeOnTime: Boolean,
    val durationMet: Boolean,
    val totalMinutesAwarded: Int
)
