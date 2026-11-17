package com.earnreels.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.earnreels.app.data.local.EarnReelsDatabase
import com.earnreels.app.data.preferences.UserPreferences
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.domain.usecase.ResetDailyAllowanceUseCase
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class MidnightResetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val db = EarnReelsDatabase.getInstance(applicationContext)
            val rewardsRepo = RewardsRepository(db.reelsAllowanceDao(), db.rewardTransactionDao())
            val resetUseCase = ResetDailyAllowanceUseCase(rewardsRepo)
            val preferences = UserPreferences(applicationContext)

            val settings = preferences.appSettingsFlow.first()
            val today = LocalDate.now().toString()

            resetUseCase(today, settings.dailyReelsCap)

            // Schedule next reset
            scheduleMidnightReset(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "earnreels_midnight_reset"

        fun scheduleMidnightReset(context: Context) {
            val now = LocalDateTime.now()
            val midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT)
            val initialDelayMillis = Duration.between(now, midnight).toMillis().coerceAtLeast(0)

            val resetRequest = OneTimeWorkRequestBuilder<MidnightResetWorker>()
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                resetRequest
            )
        }
    }
}
