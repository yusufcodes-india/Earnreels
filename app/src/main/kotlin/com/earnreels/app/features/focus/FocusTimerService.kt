package com.earnreels.app.features.focus

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.earnreels.app.data.local.EarnReelsDatabase
import com.earnreels.app.data.repository.FocusRepository
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.data.repository.StreakRepository
import com.earnreels.app.data.repository.StudyGoalRepository
import com.earnreels.app.domain.usecase.CompleteFocusSessionUseCase
import com.earnreels.app.notifications.FocusNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

class FocusTimerService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private var durationMinutes = 25
    private var totalSeconds = 25 * 60
    private var remainingSeconds = totalSeconds
    private var sessionId: Long = 0
    private var linkedGoalId: Long = 0
    private var linkedGoalTitle: String? = null

    override fun onCreate() {
        super.onCreate()
        FocusNotificationHelper.createNotificationChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                durationMinutes = intent.getIntExtra(EXTRA_DURATION_MINUTES, 25)
                sessionId = intent.getLongExtra(EXTRA_SESSION_ID, 0)
                linkedGoalId = intent.getLongExtra(EXTRA_LINKED_GOAL_ID, 0)
                linkedGoalTitle = intent.getStringExtra(EXTRA_LINKED_GOAL_TITLE)

                totalSeconds = durationMinutes * 60
                remainingSeconds = totalSeconds
                startCountdown()
            }
            ACTION_PAUSE -> pauseCountdown()
            ACTION_RESUME -> resumeCountdown()
            ACTION_CANCEL -> cancelCountdown()
        }
        return START_NOT_STICKY
    }

    private fun startCountdown() {
        timerJob?.cancel()
        _statusFlow.value = TimerStatus.RUNNING
        _remainingSecondsFlow.value = remainingSeconds
        _totalSecondsFlow.value = totalSeconds
        _activeGoalTitleFlow.value = linkedGoalTitle

        val notification = FocusNotificationHelper.buildNotification(
            this, remainingSeconds, totalSeconds, isPaused = false
        )
        startForeground(FocusNotificationHelper.NOTIFICATION_ID, notification)

        timerJob = serviceScope.launch {
            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
                _remainingSecondsFlow.value = remainingSeconds

                val updatedNotification = FocusNotificationHelper.buildNotification(
                    this@FocusTimerService, remainingSeconds, totalSeconds, isPaused = false
                )
                startForeground(FocusNotificationHelper.NOTIFICATION_ID, updatedNotification)
            }

            // Session completed!
            _statusFlow.value = TimerStatus.COMPLETED
            onSessionFinished()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun pauseCountdown() {
        timerJob?.cancel()
        _statusFlow.value = TimerStatus.PAUSED
        val notification = FocusNotificationHelper.buildNotification(
            this, remainingSeconds, totalSeconds, isPaused = true
        )
        startForeground(FocusNotificationHelper.NOTIFICATION_ID, notification)
    }

    private fun resumeCountdown() {
        _statusFlow.value = TimerStatus.RUNNING
        timerJob = serviceScope.launch {
            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
                _remainingSecondsFlow.value = remainingSeconds

                val updatedNotification = FocusNotificationHelper.buildNotification(
                    this@FocusTimerService, remainingSeconds, totalSeconds, isPaused = false
                )
                startForeground(FocusNotificationHelper.NOTIFICATION_ID, updatedNotification)
            }

            _statusFlow.value = TimerStatus.COMPLETED
            onSessionFinished()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun cancelCountdown() {
        timerJob?.cancel()
        _statusFlow.value = TimerStatus.IDLE
        remainingSeconds = totalSeconds
        _remainingSecondsFlow.value = totalSeconds
        _activeGoalTitleFlow.value = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun onSessionFinished() {
        serviceScope.launch(Dispatchers.IO) {
            val db = EarnReelsDatabase.getInstance(applicationContext)
            val focusRepo = FocusRepository(db.focusSessionDao())
            val rewardsRepo = RewardsRepository(db.reelsAllowanceDao(), db.rewardTransactionDao())
            val streakRepo = StreakRepository(db.streakDao(), rewardsRepo)
            val goalRepo = StudyGoalRepository(db.studyGoalDao())
            val completeUseCase = CompleteFocusSessionUseCase(focusRepo, rewardsRepo, streakRepo, goalRepo)

            completeUseCase(sessionId, durationMinutes, if (linkedGoalId > 0) linkedGoalId else null)
            _activeGoalTitleFlow.value = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.earnreels.app.action.START_FOCUS"
        const val ACTION_PAUSE = "com.earnreels.app.action.PAUSE_FOCUS"
        const val ACTION_RESUME = "com.earnreels.app.action.RESUME_FOCUS"
        const val ACTION_CANCEL = "com.earnreels.app.action.CANCEL_FOCUS"

        const val EXTRA_DURATION_MINUTES = "extra_duration_minutes"
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val EXTRA_LINKED_GOAL_ID = "extra_linked_goal_id"
        const val EXTRA_LINKED_GOAL_TITLE = "extra_linked_goal_title"

        private val _statusFlow = MutableStateFlow(TimerStatus.IDLE)
        val statusFlow: StateFlow<TimerStatus> = _statusFlow.asStateFlow()

        private val _remainingSecondsFlow = MutableStateFlow(25 * 60)
        val remainingSecondsFlow: StateFlow<Int> = _remainingSecondsFlow.asStateFlow()

        private val _totalSecondsFlow = MutableStateFlow(25 * 60)
        val totalSecondsFlow: StateFlow<Int> = _totalSecondsFlow.asStateFlow()

        private val _activeGoalTitleFlow = MutableStateFlow<String?>(null)
        val activeGoalTitleFlow: StateFlow<String?> = _activeGoalTitleFlow.asStateFlow()

        fun start(
            context: Context,
            durationMinutes: Int,
            sessionId: Long,
            linkedGoalId: Long? = null,
            linkedGoalTitle: String? = null
        ) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DURATION_MINUTES, durationMinutes)
                putExtra(EXTRA_SESSION_ID, sessionId)
                if (linkedGoalId != null) putExtra(EXTRA_LINKED_GOAL_ID, linkedGoalId)
                if (linkedGoalTitle != null) putExtra(EXTRA_LINKED_GOAL_TITLE, linkedGoalTitle)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pause(context: Context) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(intent)
        }

        fun resume(context: Context) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_RESUME
            }
            context.startService(intent)
        }

        fun cancel(context: Context) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_CANCEL
            }
            context.startService(intent)
        }
    }
}
