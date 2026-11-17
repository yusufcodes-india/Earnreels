package com.earnreels.app.features.today

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earnreels.app.data.local.EarnReelsDatabase
import com.earnreels.app.data.preferences.UserPreferences
import com.earnreels.app.data.repository.FocusRepository
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.data.repository.SleepRepository
import com.earnreels.app.data.repository.StepsRepository
import com.earnreels.app.data.repository.StreakRepository
import com.earnreels.app.data.repository.WorkoutRepository
import com.earnreels.app.domain.model.AppSettings
import com.earnreels.app.domain.model.DailyHabitSummary
import com.earnreels.app.domain.model.ReelsAllowance
import com.earnreels.app.domain.model.WorkoutType
import com.earnreels.app.health.HealthConnectManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TodayUiState(
    val date: String = LocalDate.now().toString(),
    val allowance: ReelsAllowance = ReelsAllowance(date = LocalDate.now().toString()),
    val summary: DailyHabitSummary = DailyHabitSummary(date = LocalDate.now().toString()),
    val settings: AppSettings = AppSettings(),
    val combinedStreakDays: Int = 0,
    val focusStreakDays: Int = 0,
    val workoutCompletedToday: Boolean = false,
    val healthConnectAvailable: Boolean = false,
    val isWorkoutDialogOpen: Boolean = false
)

class TodayViewModel(
    private val rewardsRepository: RewardsRepository,
    private val stepsRepository: StepsRepository,
    private val sleepRepository: SleepRepository,
    private val workoutRepository: WorkoutRepository,
    private val streakRepository: StreakRepository,
    private val focusRepository: FocusRepository,
    private val preferences: UserPreferences,
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val today = LocalDate.now().toString()

    val allowance: StateFlow<ReelsAllowance> = rewardsRepository.getAllowanceForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReelsAllowance(date = today))

    val summary: StateFlow<DailyHabitSummary?> = stepsRepository.getSummaryForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val settings: StateFlow<AppSettings> = preferences.appSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val combinedStreak: StateFlow<Int> = streakRepository.getStreak(StreakRepository.COMBINED_HABIT)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        .let { flow ->
            MutableStateFlow(0)
        }

    val completedFocusMinutes: StateFlow<Int> = focusRepository.getTotalCompletedFocusMinutes(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isWorkoutDialogOpen = MutableStateFlow(false)
    val isWorkoutDialogOpen: StateFlow<Boolean> = _isWorkoutDialogOpen.asStateFlow()

    fun refreshHealthData() {
        viewModelScope.launch {
            if (healthConnectManager.hasAllPermissions()) {
                // Steps
                val currentSteps = healthConnectManager.readTodaySteps().toInt()
                val currentSettings = settings.value
                stepsRepository.updateSteps(today, currentSteps, currentSettings.dailyStepGoal)

                // Sleep
                val sleep = healthConnectManager.readLastNightSleepSession()
                if (sleep != null) {
                    sleepRepository.evaluateSleepSession(
                        date = today,
                        startTimeEpochMillis = sleep.startTime.toEpochMilli(),
                        endTimeEpochMillis = sleep.endTime.toEpochMilli(),
                        targetBedtimeHour = currentSettings.targetBedtimeHour,
                        targetBedtimeMinute = currentSettings.targetBedtimeMinute,
                        minDurationMinutes = currentSettings.minSleepDurationMinutes
                    )
                }
            }
        }
    }

    fun openWorkoutDialog() {
        _isWorkoutDialogOpen.value = true
    }

    fun closeWorkoutDialog() {
        _isWorkoutDialogOpen.value = false
    }

    fun logWorkout(type: WorkoutType) {
        viewModelScope.launch {
            val awarded = workoutRepository.logWorkout(today, type)
            if (awarded > 0) {
                streakRepository.recordHabitCompletion(StreakRepository.WORKOUT, today)
            }
            closeWorkoutDialog()
        }
    }

    companion object {
        fun create(context: Context): TodayViewModel {
            val db = EarnReelsDatabase.getInstance(context)
            val rewardsRepo = RewardsRepository(db.reelsAllowanceDao(), db.rewardTransactionDao())
            val stepsRepo = StepsRepository(db.dailyHabitSummaryDao(), rewardsRepo)
            val sleepRepo = SleepRepository(db.dailyHabitSummaryDao(), rewardsRepo)
            val workoutRepo = WorkoutRepository(db.workoutLogDao(), db.dailyHabitSummaryDao(), rewardsRepo)
            val streakRepo = StreakRepository(db.streakDao(), rewardsRepo)
            val focusRepo = FocusRepository(db.focusSessionDao())
            val prefs = UserPreferences(context)
            val healthManager = HealthConnectManager(context)

            return TodayViewModel(
                rewardsRepo, stepsRepo, sleepRepo, workoutRepo, streakRepo, focusRepo, prefs, healthManager
            )
        }
    }
}
