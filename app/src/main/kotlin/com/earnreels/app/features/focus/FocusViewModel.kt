package com.earnreels.app.features.focus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earnreels.app.data.repository.FocusRepository
import com.earnreels.app.data.repository.StreakRepository
import com.earnreels.app.data.repository.StudyGoalRepository
import com.earnreels.app.domain.model.StudyGoal
import com.earnreels.app.domain.model.StudyGoalCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class FocusViewModel(
    private val focusRepository: FocusRepository,
    private val streakRepository: StreakRepository,
    private val studyGoalRepository: StudyGoalRepository? = null
) : ViewModel() {

    private val _selectedDuration = MutableStateFlow(25)
    val selectedDuration: StateFlow<Int> = _selectedDuration.asStateFlow()

    private val _linkedGoalId = MutableStateFlow<Long?>(null)
    val linkedGoalId: StateFlow<Long?> = _linkedGoalId.asStateFlow()

    private val _linkedGoalTitle = MutableStateFlow<String?>(null)
    val linkedGoalTitle: StateFlow<String?> = _linkedGoalTitle.asStateFlow()

    private val today = LocalDate.now().toString()

    val studyGoals: StateFlow<List<StudyGoal>> = (studyGoalRepository?.getGoalsForDate(today))
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    val todayCompletedMinutes: StateFlow<Int> = focusRepository.getTotalCompletedFocusMinutes(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val focusStreak = streakRepository.getStreak(StreakRepository.FOCUS)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val timerStatus: StateFlow<TimerStatus> = FocusTimerService.statusFlow
    val remainingSeconds: StateFlow<Int> = FocusTimerService.remainingSecondsFlow
    val totalSeconds: StateFlow<Int> = FocusTimerService.totalSecondsFlow
    val activeGoalTitle: StateFlow<String?> = FocusTimerService.activeGoalTitleFlow

    private val _isAddGoalDialogOpen = MutableStateFlow(false)
    val isAddGoalDialogOpen: StateFlow<Boolean> = _isAddGoalDialogOpen.asStateFlow()

    fun openAddGoalDialog() {
        _isAddGoalDialogOpen.value = true
    }

    fun closeAddGoalDialog() {
        _isAddGoalDialogOpen.value = false
    }

    fun addStudyGoal(title: String, targetMinutes: Int, category: StudyGoalCategory) {
        if (title.isBlank() || targetMinutes <= 0) return
        viewModelScope.launch {
            val id = studyGoalRepository?.addGoal(
                title = title.trim(),
                targetMinutes = targetMinutes,
                category = category,
                date = today
            )
            if (id != null && id > 0) {
                setLinkedGoal(id, title.trim())
            }
            closeAddGoalDialog()
        }
    }

    fun deleteStudyGoal(goalId: Long) {
        viewModelScope.launch {
            studyGoalRepository?.deleteGoal(goalId)
            if (_linkedGoalId.value == goalId) {
                setLinkedGoal(null, null)
            }
        }
    }

    fun setLinkedGoal(goalId: Long?, title: String?) {
        _linkedGoalId.value = goalId
        _linkedGoalTitle.value = title
    }

    fun selectDuration(minutes: Int) {
        if (timerStatus.value == TimerStatus.IDLE) {
            _selectedDuration.value = minutes
        }
    }

    fun startSession(context: Context) {
        viewModelScope.launch {
            val sessionId = focusRepository.startSession(_selectedDuration.value)
            FocusTimerService.start(
                context = context,
                durationMinutes = _selectedDuration.value,
                sessionId = sessionId,
                linkedGoalId = _linkedGoalId.value,
                linkedGoalTitle = _linkedGoalTitle.value
            )
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
