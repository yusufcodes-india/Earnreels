package com.earnreels.app.features.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.earnreels.app.domain.model.AppSettings
import com.earnreels.app.domain.model.DailyHabitSummary
import com.earnreels.app.domain.model.ReelsAllowance
import com.earnreels.app.ui.components.BottomNavBar
import com.earnreels.app.ui.components.HabitCard
import com.earnreels.app.ui.components.NavTab
import com.earnreels.app.ui.components.PrimaryButton
import com.earnreels.app.ui.components.ReelsWalletRing
import com.earnreels.app.ui.components.RewardCard
import com.earnreels.app.ui.theme.EarnReelsTheme
import com.earnreels.app.ui.theme.GraphiteBackground
import com.earnreels.app.ui.theme.PrimaryText
import com.earnreels.app.ui.theme.ProgressAccent
import com.earnreels.app.ui.theme.SecondaryText
import com.earnreels.app.ui.theme.SleepAccent
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.SuccessAccent

/**
 * Calm Graphite Home Screen:
 * Centers exclusively on one question: "How much Reels time do I have, and how do I earn more?"
 *
 * Minimal composition:
 * 1. App title "EarnReels" and settings icon
 * 2. Large thin circular progress ring (Reels Wallet)
 * 3. Primary button: "Start Focus"
 * 4. Three compact neutral cards: Focus, Steps, Sleep
 * 5. Today's reward card showing the next achievable reward
 * 6. Bottom navigation: Today, Rewards, History
 */
@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onStartFocus: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val allowance by viewModel.allowance.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val completedFocus by viewModel.completedFocusMinutes.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshHealthData()
    }

    TodayDashboardContent(
        allowance = allowance,
        summary = summary,
        settings = settings,
        completedFocusMinutes = completedFocus,
        onStartFocus = onStartFocus,
        onOpenSettings = onOpenSettings,
        onSelectTab = onSelectTab,
        modifier = modifier
    )
}

@Composable
fun TodayDashboardContent(
    allowance: ReelsAllowance,
    summary: DailyHabitSummary?,
    settings: AppSettings,
    completedFocusMinutes: Int,
    onStartFocus: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = NavTab.TODAY,
                onTabSelected = onSelectTab
            )
        },
        containerColor = GraphiteBackground,
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

            // 1. App Title & Settings Icon (Uncluttered)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EarnReels",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText
                )

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.padding(Spacing.fine)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings",
                        tint = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 2. Large Thin Circular Progress Ring (Centerpiece)
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

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 4. Three Compact Neutral Habit Cards: Focus, Steps, Sleep
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Focus Card
                val focusBlocks = completedFocusMinutes / 25
                HabitCard(
                    title = "Focus",
                    icon = Icons.Rounded.HourglassEmpty,
                    primaryMetric = "$completedFocusMinutes min",
                    secondaryMetric = if (focusBlocks > 0) "$focusBlocks block${if (focusBlocks > 1) "s" else ""} done" else "Goal: 25m blocks",
                    progress = (completedFocusMinutes.toFloat() / 25f).coerceIn(0f, 1f),
                    accentColor = ProgressAccent,
                    onClick = onStartFocus
                )

                // Steps Card
                val steps = summary?.stepsCompleted ?: 0
                val stepGoal = settings.dailyStepGoal
                val stepsReached = steps >= stepGoal
                HabitCard(
                    title = "Steps",
                    icon = Icons.AutoMirrored.Rounded.DirectionsRun,
                    primaryMetric = "%,d / %,d".format(steps, stepGoal),
                    secondaryMetric = if (stepsReached) "Goal reached" else "Health Connect",
                    progress = if (stepGoal > 0) (steps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f) else 0f,
                    accentColor = if (stepsReached) SuccessAccent else ProgressAccent
                )

                // Sleep Card
                val sleepMinutes = summary?.sleepDurationMinutes ?: 0
                val sleepHours = sleepMinutes / 60
                val sleepRemainMin = sleepMinutes % 60
                val sleepTargetMet = summary?.sleepGoalMet == true
                HabitCard(
                    title = "Sleep",
                    icon = Icons.Rounded.Bedtime,
                    primaryMetric = if (sleepMinutes > 0) "${sleepHours}h ${sleepRemainMin}m" else "No session logged",
                    secondaryMetric = if (sleepTargetMet) "Target reached" else "Target: ${settings.formattedMinSleep}",
                    progress = if (sleepMinutes > 0 && settings.minSleepDurationMinutes > 0) {
                        (sleepMinutes.toFloat() / settings.minSleepDurationMinutes.toFloat()).coerceIn(0f, 1f)
                    } else 0f,
                    accentColor = SleepAccent
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 5. Today's Reward Card: Next Achievable Action
            RewardCard(
                rewardTitle = "Today's reward",
                rewardDescription = if (allowance.canEarnMore) "Complete a 25-minute focus session" else "Daily cap reached (30 min)",
                rewardAmountMinutes = if (allowance.canEarnMore) 5 else 0
            )

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodayDashboardPreview() {
    EarnReelsTheme {
        TodayDashboardContent(
            allowance = ReelsAllowance(
                date = "2026-11-17",
                currentAllowanceMinutes = 12,
                totalEarnedToday = 15,
                totalUsedToday = 3,
                maxDailyCap = 30
            ),
            summary = DailyHabitSummary(
                date = "2026-11-17",
                focusMinutesCompleted = 25,
                stepsCompleted = 6240,
                stepGoal = 8000,
                stepRewardAwarded = false,
                workoutCompleted = false,
                sleepBedtimeOnTime = true,
                sleepDurationMinutes = 435,
                sleepGoalMet = true
            ),
            settings = AppSettings(),
            completedFocusMinutes = 25,
            onStartFocus = {},
            onOpenSettings = {},
            onSelectTab = {}
        )
    }
}
