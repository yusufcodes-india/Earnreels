package com.earnreels.app.features.focus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earnreels.app.data.repository.FocusRepository
import com.earnreels.app.data.repository.StreakRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FocusUiState(
    val selectedDurationMinutes: Int = 25,
    val status: TimerStatus = TimerStatus.IDLE,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val todayCompletedMinutes: Int = 0,
    val focusStreakDays: Int = 0
)

class FocusViewModel(
    private val focusRepository: FocusRepository,
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _selectedDuration = MutableStateFlow(25)
    val selectedDuration: StateFlow<Int> = _selectedDuration.asStateFlow()

    private val today = LocalDate.now().toString()

    val todayCompletedMinutes: StateFlow<Int> = focusRepository.getTotalCompletedFocusMinutes(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val focusStreak = streakRepository.getStreak(StreakRepository.FOCUS)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val timerStatus: StateFlow<TimerStatus> = FocusTimerService.statusFlow
    val remainingSeconds: StateFlow<Int> = FocusTimerService.remainingSecondsFlow
    val totalSeconds: StateFlow<Int> = FocusTimerService.totalSecondsFlow

    fun selectDuration(minutes: Int) {
        if (timerStatus.value == TimerStatus.IDLE) {
            _selectedDuration.value = minutes
        }
    }

    fun startSession(context: Context) {
        viewModelScope.launch {
            val sessionId = focusRepository.startSession(_selectedDuration.value)
            FocusTimerService.start(context, _selectedDuration.value, sessionId)
        }
    }

    fun pauseSession(context: Context) {
        FocusTimerService.pause(context)
    }

    fun resumeSession(context: Context) {
        FocusTimerService.resume(context)
    }

    fun cancelSession(context: Context) {
        FocusTimerService.cancel(context)
    }
}
