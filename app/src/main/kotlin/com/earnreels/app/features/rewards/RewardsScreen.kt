package com.earnreels.app.features.rewards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.earnreels.app.domain.model.HabitRewardType
import com.earnreels.app.ui.components.BottomNavBar
import com.earnreels.app.ui.components.NavTab
import com.earnreels.app.ui.components.ReelsWalletRing
import com.earnreels.app.ui.components.RewardCard
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.CardShape
import com.earnreels.app.ui.theme.CardSurface
import com.earnreels.app.ui.theme.CharcoalBackground
import com.earnreels.app.ui.theme.LimeAccent
import com.earnreels.app.ui.theme.PillShape
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.TextMain
import com.earnreels.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RewardsScreen(
    viewModel: RewardsViewModel,
    onSelectTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val allowance by viewModel.allowance.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = NavTab.REWARDS,
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

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Rewards & Wallet",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextMain
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Wallet Ring
            ReelsWalletRing(
                remainingMinutes = allowance.currentAllowanceMinutes,
                maxMinutes = settings.dailyReelsCap
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            // Daily Summary Stats Card
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.cardPadding),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Earned Today",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(Spacing.fine))
                        Text(
                            text = "+${allowance.totalEarnedToday} m",
                            style = MaterialTheme.typography.titleMedium,
                            color = LimeAccent
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Used Today",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(Spacing.fine))
                        Text(
                            text = "-${allowance.totalUsedToday} m",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextMain
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Daily Cap",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(Spacing.fine))
                        Text(
                            text = "${settings.dailyReelsCap} m",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.base))

            // Midnight Expiry Notice
            Text(
                text = "⚡ Unused allowance resets tonight at midnight.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Section: Ways to Earn More
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Ways to Earn",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextMain
                )
            }
            Spacer(modifier = Modifier.height(Spacing.md))

            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                RewardCard(
                    rewardTitle = "Focus Block",
                    rewardDescription = "15m (+3m), 25m (+5m), 45m (+8m), 60m (+10m)",
                    rewardAmountMinutes = 5
                )
                RewardCard(
                    rewardTitle = "Daily Steps",
                    rewardDescription = "Hit your ${settings.dailyStepGoal} daily steps goal",
                    rewardAmountMinutes = HabitRewardType.STEPS_GOAL.defaultMinutes
                )
                RewardCard(
                    rewardTitle = "Workout",
                    rewardDescription = "Complete and log 1 workout session today",
                    rewardAmountMinutes = HabitRewardType.WORKOUT.defaultMinutes
                )
                RewardCard(
                    rewardTitle = "Bedtime Routine",
                    rewardDescription = "Fall asleep within 30m of ${settings.formattedBedtime}",
                    rewardAmountMinutes = HabitRewardType.SLEEP_BEDTIME.defaultMinutes
                )
                RewardCard(
                    rewardTitle = "Sleep Duration",
                    rewardDescription = "Meet minimum ${settings.formattedMinSleep} sleep",
                    rewardAmountMinutes = HabitRewardType.SLEEP_DURATION.defaultMinutes
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Section: Today's Transactions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Today's Activity",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextMain
                )
            }
            Spacer(modifier = Modifier.height(Spacing.md))

            if (transactions.isEmpty()) {
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.cardPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No rewards earned yet today. Complete a focus block to get started!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                    transactions.forEach { tx ->
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.description,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextMain
                                    )
                                    Text(
                                        text = timeFormat.format(Date(tx.timestamp)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }

                                Text(
                                    text = "+${tx.amountMinutes} m",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = LimeAccent
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}
