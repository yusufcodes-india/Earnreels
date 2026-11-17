package com.earnreels.app.features.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earnreels.app.data.local.EarnReelsDatabase
import com.earnreels.app.data.preferences.UserPreferences
import com.earnreels.app.domain.model.AppSettings
import com.earnreels.app.health.HealthConnectManager
import com.earnreels.app.health.HealthConnectStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: UserPreferences,
    private val healthConnectManager: HealthConnectManager,
    private val db: EarnReelsDatabase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = preferences.appSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    private val _healthConnectStatus = MutableStateFlow(healthConnectManager.checkAvailability())
    val healthConnectStatus: StateFlow<HealthConnectStatus> = _healthConnectStatus.asStateFlow()

    private val _hasHealthPermissions = MutableStateFlow(false)
    val hasHealthPermissions: StateFlow<Boolean> = _hasHealthPermissions.asStateFlow()

    init {
        checkHealthPermissions()
    }

    fun checkHealthPermissions() {
        viewModelScope.launch {
            _hasHealthPermissions.value = healthConnectManager.hasAllPermissions()
        }
    }

    fun updateDailyStepGoal(goal: Int) {
        viewModelScope.launch {
            preferences.updateDailyStepGoal(goal)
        }
    }

    fun updateTargetBedtime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferences.updateTargetBedtime(hour, minute)
        }
    }

    fun updateMinSleepDuration(minutes: Int) {
        viewModelScope.launch {
            preferences.updateMinSleepDuration(minutes)
        }
    }

    fun updateDailyReelsCap(cap: Int) {
        viewModelScope.launch {
            preferences.updateDailyReelsCap(cap)
        }
    }

    fun setBlockingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setBlockingEnabled(enabled)
        }
    }

    fun setStrictMode(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setStrictMode(enabled)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            db.clearAllTables()
            preferences.clearAll()
        }
    }

    companion object {
        fun create(context: Context): SettingsViewModel {
            val prefs = UserPreferences(context)
            val healthManager = HealthConnectManager(context)
            val db = EarnReelsDatabase.getInstance(context)
            return SettingsViewModel(prefs, healthManager, db)
        }
    }
}
