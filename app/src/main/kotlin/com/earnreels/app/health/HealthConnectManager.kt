package com.earnreels.app.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

enum class HealthConnectStatus {
    AVAILABLE,
    NOT_INSTALLED,
    NOT_SUPPORTED
}

data class SleepSessionResult(
    val startTime: Instant,
    val endTime: Instant,
    val durationMinutes: Long
)

class HealthConnectManager(private val context: Context) {

    val healthConnectClient: HealthConnectClient? by lazy {
        if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else {
            null
        }
    }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )

    fun checkAvailability(): HealthConnectStatus {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectStatus.AVAILABLE
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectStatus.NOT_INSTALLED
            else -> HealthConnectStatus.NOT_SUPPORTED
        }
    }

    suspend fun hasAllPermissions(): Boolean {
        val client = healthConnectClient ?: return false
        val granted = client.permissionController.getGrantedPermissions()
        return granted.containsAll(permissions)
    }

    /**
     * Reads today's total steps using aggregate query to avoid double counting.
     */
    suspend fun readTodaySteps(): Long {
        val client = healthConnectClient ?: return 0L
        val zone = ZoneId.systemDefault()
        val startOfToday = LocalDate.now().atStartOfDay(zone).toInstant()
        val endOfToday = Instant.now()

        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startOfToday, endOfToday)
                )
            )
            response[StepsRecord.COUNT_TOTAL] ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Reads the most recent sleep session from previous evening (18:00) through current afternoon (14:00).
     */
    suspend fun readLastNightSleepSession(): SleepSessionResult? {
        val client = healthConnectClient ?: return null
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now()
        val windowStart = now.toLocalDate().minusDays(1).atTime(18, 0).atZone(zone).toInstant()
        val windowEnd = now.toLocalDate().atTime(14, 0).atZone(zone).toInstant()

        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(windowStart, windowEnd)
                )
            )
            val mostRecent = response.records.maxByOrNull { it.endTime } ?: return null
            val durationMinutes = ChronoUnit.MINUTES.between(mostRecent.startTime, mostRecent.endTime)

            SleepSessionResult(
                startTime = mostRecent.startTime,
                endTime = mostRecent.endTime,
                durationMinutes = durationMinutes.coerceAtLeast(0)
            )
        } catch (e: Exception) {
            null
        }
    }
}
