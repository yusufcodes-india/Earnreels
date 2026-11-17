package com.earnreels.app.features.today

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.earnreels.app.domain.model.AppSettings
import com.earnreels.app.domain.model.DailyHabitSummary
import com.earnreels.app.domain.model.ReelsAllowance
import com.earnreels.app.domain.model.StudyGoal
import com.earnreels.app.domain.model.StudyGoalCategory
import com.earnreels.app.domain.model.WorkoutType
import com.earnreels.app.ui.components.BottomNavBar
import com.earnreels.app.ui.components.HabitCard
import com.earnreels.app.ui.components.NavTab
import com.earnreels.app.ui.components.PrimaryButton
import com.earnreels.app.ui.components.ReelsWalletRing
import com.earnreels.app.ui.components.RewardCard
import com.earnreels.app.ui.components.StreakBadge
import com.earnreels.app.ui.components.StudyGoalCard
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.CardShape
import com.earnreels.app.ui.theme.CardSurface
import com.earnreels.app.ui.theme.CharcoalBackground
import com.earnreels.app.ui.theme.EarnReelsTheme
import com.earnreels.app.ui.theme.LimeAccent
import com.earnreels.app.ui.theme.PillShape
import com.earnreels.app.ui.theme.SleepAccent
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.TextMain
import com.earnreels.app.ui.theme.TextSecondary

@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onStartFocus: () -> Unit,
    onStartGoalFocus: (goalId: Long, goalTitle: String, durationMinutes: Int) -> Unit,
    onOpenSettings: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val allowance by viewModel.allowance.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val studyGoals by viewModel.studyGoals.collectAsStateWithLifecycle()
    val completedFocus by viewModel.completedFocusMinutes.collectAsStateWithLifecycle()
    val isWorkoutDialogOpen by viewModel.isWorkoutDialogOpen.collectAsStateWithLifecycle()
    val isAddGoalDialogOpen by viewModel.isAddGoalDialogOpen.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshHealthData()
    }

    TodayDashboardContent(
        allowance = allowance,
        summary = summary,
        settings = settings,
        studyGoals = studyGoals,
        completedFocusMinutes = completedFocus,
        isWorkoutDialogOpen = isWorkoutDialogOpen,
        isAddGoalDialogOpen = isAddGoalDialogOpen,
        onStartFocus = onStartFocus,
        onStartGoalFocus = onStartGoalFocus,
        onOpenSettings = onOpenSettings,
        onSelectTab = onSelectTab,
        onOpenWorkoutDialog = { viewModel.openWorkoutDialog() },
        onCloseWorkoutDialog = { viewModel.closeWorkoutDialog() },
        onLogWorkout = { viewModel.logWorkout(it) },
        onOpenAddGoalDialog = { viewModel.openAddGoalDialog() },
        onCloseAddGoalDialog = { viewModel.closeAddGoalDialog() },
        onAddStudyGoal = { title, target, category -> viewModel.addStudyGoal(title, target, category) },
        onDeleteStudyGoal = { viewModel.deleteStudyGoal(it) },
        modifier = modifier
    )
}

@Composable
fun TodayDashboardContent(
    allowance: ReelsAllowance,
    summary: DailyHabitSummary?,
    settings: AppSettings,
    studyGoals: List<StudyGoal>,
    completedFocusMinutes: Int,
    isWorkoutDialogOpen: Boolean,
    isAddGoalDialogOpen: Boolean,
    onStartFocus: () -> Unit,
    onStartGoalFocus: (Long, String, Int) -> Unit,
    onOpenSettings: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
    onOpenWorkoutDialog: () -> Unit,
    onCloseWorkoutDialog: () -> Unit,
    onLogWorkout: (WorkoutType) -> Unit,
    onOpenAddGoalDialog: () -> Unit,
    onCloseAddGoalDialog: () -> Unit,
    onAddStudyGoal: (String, Int, StudyGoalCategory) -> Unit,
    onDeleteStudyGoal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = NavTab.TODAY,
                onTabSelected = onSelectTab
            )
        },
        containerColor = CharcoalBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenMargin),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.base))

            // 1. App Title & Settings Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EarnReels",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextMain
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StreakBadge(streakDays = 1)
                    Spacer(modifier = Modifier.size(Spacing.sm))
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 2. Large Circular Progress Ring
            ReelsWalletRing(
                remainingMinutes = allowance.currentAllowanceMinutes,
                maxMinutes = settings.dailyReelsCap
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 3. Primary Button: Start Focus
            PrimaryButton(
                text = "Start Focus",
                onClick = onStartFocus
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 4. Three Compact Habit Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Focus Card
                HabitCard(
                    title = "Focus",
                    icon = Icons.Rounded.HourglassEmpty,
                    primaryMetric = "$completedFocusMinutes minutes completed",
                    secondaryMetric = "Goal: 25m blocks",
                    progress = (completedFocusMinutes.toFloat() / 25f).coerceIn(0f, 1f),
                    accentColor = LimeAccent,
                    onClick = onStartFocus
                )

                // Steps Card
                val steps = summary?.stepsCompleted ?: 0
                val stepGoal = settings.dailyStepGoal
                HabitCard(
                    title = "Steps",
                    icon = Icons.AutoMirrored.Rounded.DirectionsRun,
                    primaryMetric = "%,d / %,d steps".format(steps, stepGoal),
                    secondaryMetric = if (steps >= stepGoal) "Goal reached (+10m)" else "Health Connect",
                    progress = (steps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f),
                    accentColor = LimeAccent
                )

                // Sleep Card
                val sleepMinutes = summary?.sleepDurationMinutes ?: 0
                val sleepHours = sleepMinutes / 60
                val sleepRemainMin = sleepMinutes % 60
                val bedtimeStatus = if (summary?.sleepBedtimeOnTime == true) "On time" else "Bedtime: ${settings.formattedBedtime}"
                HabitCard(
                    title = "Sleep Routine",
                    icon = Icons.Rounded.Bedtime,
                    primaryMetric = if (sleepMinutes > 0) "${sleepHours}h ${sleepRemainMin}m • $bedtimeStatus" else "No recent session",
                    secondaryMetric = "Target: ${settings.formattedMinSleep}",
                    progress = if (sleepMinutes > 0) (sleepMinutes.toFloat() / settings.minSleepDurationMinutes.toFloat()).coerceIn(0f, 1f) else 0f,
                    accentColor = SleepAccent
                )

                // Quick Workout Completion Card
                val workoutDone = summary?.workoutCompleted == true
                HabitCard(
                    title = "Workout",
                    icon = Icons.Rounded.FitnessCenter,
                    primaryMetric = if (workoutDone) "Completed (${summary.workoutType ?: "Logged"})" else "Log daily workout",
                    secondaryMetric = if (workoutDone) "+15m earned" else "Tap to log (+15m)",
                    progress = if (workoutDone) 1f else 0f,
                    accentColor = LimeAccent,
                    onClick = {
                        if (!workoutDone) onOpenWorkoutDialog()
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 5. Custom Study & Lecture Goals Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Study & Lecture Goals",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMain
                )

                TextButton(onClick = onOpenAddGoalDialog) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = LimeAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(Spacing.xs))
                    Text(
                        text = "Add Goal",
                        style = MaterialTheme.typography.labelLarge,
                        color = LimeAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            if (studyGoals.isEmpty()) {
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenAddGoalDialog() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.cardPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📚 Set Your Study & Lecture Goals",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextMain
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "Add lectures, study blocks, or lessons. Run timers directly on them and watch remaining time count down.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    studyGoals.forEach { goal ->
                        StudyGoalCard(
                            goal = goal,
                            onStartFocus = {
                                onStartGoalFocus(goal.id, goal.title, goal.remainingMinutes)
                            },
                            onDelete = {
                                onDeleteStudyGoal(goal.id)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 6. Today's Reward Card
            RewardCard(
                rewardTitle = "Today's Reward",
                rewardDescription = if (allowance.canEarnMore) "Complete a focus block, goal, or workout to recharge" else "Daily cap reached (30 min)",
                rewardAmountMinutes = if (allowance.canEarnMore) 5 else 0
            )

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }

    // Workout Selection Dialog
    if (isWorkoutDialogOpen) {
        AlertDialog(
            onDismissRequest = onCloseWorkoutDialog,
            containerColor = CardSurface,
            title = {
                Text(
                    text = "Log Today's Workout",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMain
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Choose your workout type (+15 min Reels allowance):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(Spacing.fine))
                    WorkoutType.entries.forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLogWorkout(type) }
                                .padding(vertical = Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMain
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onCloseWorkoutDialog) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Add Study Goal Dialog
    if (isAddGoalDialogOpen) {
        var goalTitle by remember { mutableStateOf("") }
        var targetMinutes by remember { mutableIntStateOf(45) }
        var selectedCategory by remember { mutableStateOf(StudyGoalCategory.STUDY) }

        AlertDialog(
            onDismissRequest = onCloseAddGoalDialog,
            containerColor = CardSurface,
            title = {
                Text(
                    text = "New Study or Lecture Goal",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMain
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Set a custom goal for today. Finishing focus sessions will count down remaining time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    // Title Input
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Goal Title (e.g. Physics Lecture 4)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LimeAccent,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextMain,
                            unfocusedTextColor = TextMain,
                            focusedLabelColor = LimeAccent,
                            unfocusedLabelColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Selector
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StudyGoalCategory.entries.take(4).forEach { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) LimeAccent else CharcoalBackground,
                                        PillShape
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = Spacing.sm),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${cat.iconEmoji} ${cat.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) CharcoalBackground else TextMain,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Target Duration Selector
                    Text(
                        text = "Target Study Time",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(25, 45, 60, 90).forEach { mins ->
                            val isSelected = mins == targetMinutes
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) LimeAccent else CharcoalBackground,
                                        PillShape
                                    )
                                    .clickable { targetMinutes = mins }
                                    .padding(vertical = Spacing.sm),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mins}m",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) CharcoalBackground else TextMain
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (goalTitle.isNotBlank()) {
                            onAddStudyGoal(goalTitle, targetMinutes, selectedCategory)
                        }
                    },
                    enabled = goalTitle.isNotBlank()
                ) {
                    Text("Add Goal", color = if (goalTitle.isNotBlank()) LimeAccent else TextSecondary)
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseAddGoalDialog) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodayDashboardPreview() {
    EarnReelsTheme {
        TodayDashboardContent(
            allowance = ReelsAllowance(
                date = "2026-11-17",
                currentAllowanceMinutes = 20,
                totalEarnedToday = 25,
                totalUsedToday = 5,
                maxDailyCap = 30
            ),
            summary = DailyHabitSummary(
                date = "2026-11-17",
                focusMinutesCompleted = 25,
                stepsCompleted = 8420,
                stepGoal = 8000,
                stepRewardAwarded = true,
                workoutCompleted = false,
                sleepBedtimeOnTime = true,
                sleepDurationMinutes = 445,
                sleepGoalMet = true
            ),
            settings = AppSettings(),
            studyGoals = listOf(
                StudyGoal(
                    id = 1,
                    title = "Calculus Lecture 4",
                    targetMinutes = 60,
                    completedMinutes = 25,
                    date = "2026-11-17",
                    category = StudyGoalCategory.LECTURE,
                    isCompleted = false
                ),
                StudyGoal(
                    id = 2,
                    title = "Organic Chem Reading",
                    targetMinutes = 30,
                    completedMinutes = 30,
                    date = "2026-11-17",
                    category = StudyGoalCategory.STUDY,
                    isCompleted = true
                )
            ),
            completedFocusMinutes = 25,
            isWorkoutDialogOpen = false,
            isAddGoalDialogOpen = false,
            onStartFocus = {},
            onStartGoalFocus = { _, _, _ -> },
            onOpenSettings = {},
            onSelectTab = {},
            onOpenWorkoutDialog = {},
            onCloseWorkoutDialog = {},
            onLogWorkout = {},
            onOpenAddGoalDialog = {},
            onCloseAddGoalDialog = {},
            onAddStudyGoal = { _, _, _ -> },
            onDeleteStudyGoal = {}
        )
    }
}
