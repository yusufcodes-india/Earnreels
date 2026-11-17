package com.earnreels.app.features.focus

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.earnreels.app.domain.model.FocusSession
import com.earnreels.app.domain.model.StudyGoal
import com.earnreels.app.domain.model.StudyGoalCategory
import com.earnreels.app.ui.components.PrimaryButton
import com.earnreels.app.ui.theme.BlockedRed
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.ButtonShape
import com.earnreels.app.ui.theme.CardShape
import com.earnreels.app.ui.theme.CardSurface
import com.earnreels.app.ui.theme.CardSurfaceVariant
import com.earnreels.app.ui.theme.EarnReelsTheme
import com.earnreels.app.ui.theme.GraphiteBackground
import com.earnreels.app.ui.theme.PillShape
import com.earnreels.app.ui.theme.PrimaryText
import com.earnreels.app.ui.theme.ProgressAccent
import com.earnreels.app.ui.theme.SecondaryText
import com.earnreels.app.ui.theme.Spacing

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
    val activeGoalTitle by viewModel.activeGoalTitle.collectAsStateWithLifecycle()
    val linkedGoalTitle by viewModel.linkedGoalTitle.collectAsStateWithLifecycle()
    val studyGoals by viewModel.studyGoals.collectAsStateWithLifecycle()
    val isAddGoalDialogOpen by viewModel.isAddGoalDialogOpen.collectAsStateWithLifecycle()

    FocusTimerContent(
        selectedDuration = selectedDuration,
        status = status,
        remainingSeconds = remainingSeconds,
        totalSeconds = totalSeconds,
        todayCompletedMinutes = todayCompletedMinutes,
        goalTitle = activeGoalTitle ?: linkedGoalTitle,
        studyGoals = studyGoals,
        isAddGoalDialogOpen = isAddGoalDialogOpen,
        onSelectDuration = { viewModel.selectDuration(it) },
        onStart = { viewModel.startSession(context) },
        onPause = { viewModel.pauseSession(context) },
        onResume = { viewModel.resumeSession(context) },
        onCancel = { viewModel.cancelSession(context) },
        onSelectGoal = { id, title -> viewModel.setLinkedGoal(id, title) },
        onOpenAddGoalDialog = { viewModel.openAddGoalDialog() },
        onCloseAddGoalDialog = { viewModel.closeAddGoalDialog() },
        onAddGoal = { title, mins, cat -> viewModel.addStudyGoal(title, mins, cat) },
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
    goalTitle: String? = null,
    studyGoals: List<StudyGoal> = emptyList(),
    isAddGoalDialogOpen: Boolean = false,
    onSelectDuration: (Int) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onSelectGoal: (Long?, String?) -> Unit = { _, _ -> },
    onOpenAddGoalDialog: () -> Unit = {},
    onCloseAddGoalDialog: () -> Unit = {},
    onAddGoal: (String, Int, StudyGoalCategory) -> Unit = { _, _, _ -> },
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
            .background(GraphiteBackground)
            .padding(horizontal = Spacing.screenMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(Spacing.base))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = SecondaryText
                    )
                }

                Text(
                    text = "Focus",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.size(48.dp)) // Equal balance
            }
        }

        // Active Linked Goal Pill
        if (!goalTitle.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .background(CardSurface, PillShape)
                    .border(1.dp, BorderSubtle, PillShape)
                    .padding(horizontal = Spacing.md, vertical = Spacing.fine)
            ) {
                Text(
                    text = "Goal: $goalTitle",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProgressAccent
                )
            }
        }

        // Thin Circular Timer Display
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            Canvas(modifier = Modifier.size(240.dp)) {
                val stroke = 8.dp.toPx()
                // Track
                drawCircle(
                    color = BorderSubtle,
                    style = Stroke(width = stroke)
                )
                // Active Progress Ring in calm blue
                drawArc(
                    color = ProgressAccent,
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
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(Spacing.fine))
                Text(
                    text = when (status) {
                        TimerStatus.RUNNING -> "In focus"
                        TimerStatus.PAUSED -> "Paused"
                        TimerStatus.COMPLETED -> "Completed"
                        TimerStatus.IDLE -> "Ready"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isRunning) ProgressAccent else SecondaryText
                )
            }
        }

        // Duration Selector (Enabled only when IDLE)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Session length",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                FocusSession.ALLOWED_DURATIONS.forEach { duration ->
                    val isSelected = duration == selectedDuration
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(
                                color = if (isSelected) CardSurfaceVariant else CardSurface,
                                shape = PillShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ProgressAccent else BorderSubtle,
                                shape = PillShape
                            )
                            .clickable(enabled = isIdle) {
                                onSelectDuration(duration)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${duration}m",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) PrimaryText else SecondaryText
                        )
                    }
                }
            }
        }

        // Action Buttons
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
                        shape = ButtonShape,
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
                                color = BlockedRed
                            )
                        }
                    }

                    // Pause / Resume Button
                    Card(
                        shape = ButtonShape,
                        colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
                        border = BorderStroke(1.dp, ProgressAccent),
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
                                tint = PrimaryText
                            )
                            Spacer(modifier = Modifier.size(Spacing.sm))
                            Text(
                                text = if (isRunning) "Pause" else "Resume",
                                style = MaterialTheme.typography.labelLarge,
                                color = PrimaryText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.base))

            // Completed Focus Today Stats Card
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = CardSurface),
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
                        color = SecondaryText
                    )
                    Text(
                        text = "$todayCompletedMinutes min",
                        style = MaterialTheme.typography.titleSmall,
                        color = PrimaryText
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
            goalTitle = "Calculus Lecture 4",
            onSelectDuration = {},
            onStart = {},
            onPause = {},
            onResume = {},
            onCancel = {},
            onBack = {}
        )
    }
}
