package com.earnreels.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.earnreels.app.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "earnreels_settings")

class UserPreferences(private val context: Context) {

    private object Keys {
        val DAILY_STEP_GOAL = intPreferencesKey("daily_step_goal")
        val TARGET_BEDTIME_HOUR = intPreferencesKey("target_bedtime_hour")
        val TARGET_BEDTIME_MINUTE = intPreferencesKey("target_bedtime_minute")
        val MIN_SLEEP_MINUTES = intPreferencesKey("min_sleep_minutes")
        val DAILY_REELS_CAP = intPreferencesKey("daily_reels_cap")
        val BLOCKING_ENABLED = booleanPreferencesKey("blocking_enabled")
        val STRICT_MODE_APP_BLOCKING = booleanPreferencesKey("strict_mode_app_blocking")

        // Customizable rewards
        val REWARD_FOCUS_15 = intPreferencesKey("reward_focus_15")
        val REWARD_FOCUS_25 = intPreferencesKey("reward_focus_25")
        val REWARD_FOCUS_45 = intPreferencesKey("reward_focus_45")
        val REWARD_FOCUS_60 = intPreferencesKey("reward_focus_60")
        val REWARD_STEPS = intPreferencesKey("reward_steps")
        val REWARD_WORKOUT = intPreferencesKey("reward_workout")
        val REWARD_BEDTIME = intPreferencesKey("reward_bedtime")
        val REWARD_SLEEP_DURATION = intPreferencesKey("reward_sleep_duration")
    }

    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            dailyStepGoal = prefs[Keys.DAILY_STEP_GOAL] ?: 8000,
            targetBedtimeHour = prefs[Keys.TARGET_BEDTIME_HOUR] ?: 23,
            targetBedtimeMinute = prefs[Keys.TARGET_BEDTIME_MINUTE] ?: 0,
            minSleepDurationMinutes = prefs[Keys.MIN_SLEEP_MINUTES] ?: 420,
            dailyReelsCap = prefs[Keys.DAILY_REELS_CAP] ?: 30,
            blockingEnabled = prefs[Keys.BLOCKING_ENABLED] ?: true,
            strictModeAppBlocking = prefs[Keys.STRICT_MODE_APP_BLOCKING] ?: false
        )
    }

    suspend fun updateDailyStepGoal(goal: Int) {
        context.dataStore.edit { it[Keys.DAILY_STEP_GOAL] = goal }
    }

    suspend fun updateTargetBedtime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.TARGET_BEDTIME_HOUR] = hour
            it[Keys.TARGET_BEDTIME_MINUTE] = minute
        }
    }

    suspend fun updateMinSleepDuration(minutes: Int) {
        context.dataStore.edit { it[Keys.MIN_SLEEP_MINUTES] = minutes }
    }

    suspend fun updateDailyReelsCap(cap: Int) {
        context.dataStore.edit { it[Keys.DAILY_REELS_CAP] = cap }
    }

    suspend fun setBlockingEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BLOCKING_ENABLED] = enabled }
    }

    suspend fun setStrictMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.STRICT_MODE_APP_BLOCKING] = enabled }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
