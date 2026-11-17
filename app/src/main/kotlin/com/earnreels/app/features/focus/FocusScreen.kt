package com.earnreels.app.features.focus

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.earnreels.app.domain.model.FocusSession
import com.earnreels.app.ui.components.PrimaryButton
import com.earnreels.app.ui.components.StreakBadge
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.CardShape
import com.earnreels.app.ui.theme.CardSurface
import com.earnreels.app.ui.theme.CardSurfaceVariant
import com.earnreels.app.ui.theme.CharcoalBackground
import com.earnreels.app.ui.theme.EarnReelsTheme
import com.earnreels.app.ui.theme.ErrorRed
import com.earnreels.app.ui.theme.LimeAccent
import com.earnreels.app.ui.theme.PillShape
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.TextMain
import com.earnreels.app.ui.theme.TextSecondary

@Composable
fun FocusScreen(
    viewModel: FocusViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDuration by viewModel.selectedDuration.collectAsStateWithLifecycle()
    val status by viewModel.timerStatus.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val totalSeconds by viewModel.totalSeconds.collectAsStateWithLifecycle()
    val todayCompletedMinutes by viewModel.todayCompletedMinutes.collectAsStateWithLifecycle()
    val streak by viewModel.focusStreak.collectAsStateWithLifecycle()

    FocusTimerContent(
        selectedDuration = selectedDuration,
        status = status,
        remainingSeconds = remainingSeconds,
        totalSeconds = totalSeconds,
        todayCompletedMinutes = todayCompletedMinutes,
        streakDays = streak?.currentStreak ?: 0,
        onSelectDuration = { viewModel.selectDuration(it) },
        onStart = { viewModel.startSession(context) },
        onPause = { viewModel.pauseSession(context) },
        onResume = { viewModel.resumeSession(context) },
        onCancel = { viewModel.cancelSession(context) },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun FocusTimerContent(
    selectedDuration: Int,
    status: TimerStatus,
    remainingSeconds: Int,
    totalSeconds: Int,
    todayCompletedMinutes: Int,
    streakDays: Int,
    onSelectDuration: (Int) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = status == TimerStatus.RUNNING
    val isPaused = status == TimerStatus.PAUSED
    val isIdle = status == TimerStatus.IDLE || status == TimerStatus.COMPLETED

    val currentTotal = if (isIdle) selectedDuration * 60 else totalSeconds
    val currentRemaining = if (isIdle) selectedDuration * 60 else remainingSeconds

    val progress = if (currentTotal > 0) {
        (currentRemaining.toFloat() / currentTotal.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "focus_timer_ring"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .padding(horizontal = Spacing.screenMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }

                Text(
                    text = "Focus Timer",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextMain
                )

                StreakBadge(streakDays = streakDays)
            }
        }

        // Circular Timer Display
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(260.dp)
        ) {
            Canvas(modifier = Modifier.size(260.dp)) {
                val stroke = 14.dp.toPx()
                // Track
                drawCircle(
                    color = BorderSubtle,
                    style = Stroke(width = stroke)
                )
                // Active Lime Ring
                drawArc(
                    color = LimeAccent,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val minutes = currentRemaining / 60
                val seconds = currentRemaining % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextMain
                )
                Spacer(modifier = Modifier.height(Spacing.fine))
                Text(
                    text = when (status) {
                        TimerStatus.RUNNING -> "STAY FOCUSED"
                        TimerStatus.PAUSED -> "PAUSED"
                        TimerStatus.COMPLETED -> "SESSION COMPLETED"
                        TimerStatus.IDLE -> "READY"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isRunning) LimeAccent else TextSecondary
                )
            }
        }

        // Duration Selector (Enabled only when IDLE)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Session Length",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                FocusSession.ALLOWED_DURATIONS.forEach { duration ->
                    val isSelected = duration == selectedDuration
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .background(
                                color = if (isSelected) LimeAccent else CardSurface,
                                shape = PillShape
                            )
                            .then(
                                if (!isSelected) Modifier.background(CardSurface, PillShape)
                                else Modifier
                            )
                            .clickable(enabled = isIdle) {
                                onSelectDuration(duration)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${duration}m",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) CharcoalBackground else TextMain
                        )
                    }
                }
            }
        }

        // Actions & Stats
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xl)
        ) {
            if (isIdle) {
                PrimaryButton(
                    text = "Start Focus (${selectedDuration} min)",
                    onClick = onStart
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    Card(
                        shape = PillShape,
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .weight(1f)
                            .height(Spacing.buttonHeight)
                            .clickable { onCancel() }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.labelLarge,
                                color = ErrorRed
                            )
                        }
                    }

                    // Pause / Resume Button
                    Card(
                        shape = PillShape,
                        colors = CardDefaults.cardColors(containerColor = LimeAccent),
                        modifier = Modifier
                            .weight(1f)
                            .height(Spacing.buttonHeight)
                            .clickable {
                                if (isRunning) onPause()
                                else onResume()
                            }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isRunning) "Pause" else "Resume",
                                tint = CharcoalBackground
                            )
                            Spacer(modifier = Modifier.size(Spacing.sm))
                            Text(
                                text = if (isRunning) "Pause" else "Resume",
                                style = MaterialTheme.typography.labelLarge,
                                color = CharcoalBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.base))

            // Completed Focus Today Stats Card
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.cardPadding, vertical = Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "$todayCompletedMinutes minutes",
                        style = MaterialTheme.typography.titleSmall,
                        color = LimeAccent
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FocusTimerPreview() {
    EarnReelsTheme {
        FocusTimerContent(
            selectedDuration = 25,
            status = TimerStatus.RUNNING,
            remainingSeconds = 21 * 60 + 40,
            totalSeconds = 25 * 60,
            todayCompletedMinutes = 25,
            streakDays = 4,
            onSelectDuration = {},
            onStart = {},
            onPause = {},
            onResume = {},
            onCancel = {},
            onBack = {}
        )
    }
}
