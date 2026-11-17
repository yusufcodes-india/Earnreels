package com.earnreels.app.features.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.earnreels.app.ui.components.BottomNavBar
import com.earnreels.app.ui.components.NavTab
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.CardShape
import com.earnreels.app.ui.theme.CardSurface
import com.earnreels.app.ui.theme.CharcoalBackground
import com.earnreels.app.ui.theme.LimeAccent
import com.earnreels.app.ui.theme.SleepAccent
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.TextMain
import com.earnreels.app.ui.theme.TextSecondary

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onSelectTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val summaries by viewModel.recentSummaries.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = NavTab.HISTORY,
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
                .padding(horizontal = Spacing.screenMargin)
        ) {
            Spacer(modifier = Modifier.height(Spacing.base))

            Text(
                text = "Past 7 Days",
                style = MaterialTheme.typography.titleLarge,
                color = TextMain
            )
            Spacer(modifier = Modifier.height(Spacing.fine))
            Text(
                text = "Your daily focus, movement, and video habits",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            if (summaries.isEmpty()) {
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
                            text = "No history recorded yet. As you complete habits, your past 7 days will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(summaries) { summary ->
                        Card(
                            shape = CardShape,
                            colors = CardDefaults.cardColors(containerColor = CardSurface),
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.cardPadding)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = summary.date,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextMain
                                    )

                                    Text(
                                        text = "+${summary.reelsMinutesEarned}m earned",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LimeAccent
                                    )
                                }

                                Spacer(modifier = Modifier.height(Spacing.md))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Column 1: Focus & Steps
                                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.fine)) {
                                        Text(
                                            text = "Focus: ${summary.focusMinutesCompleted}m",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "Steps: %,d".format(summary.stepsCompleted),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }

                                    // Column 2: Sleep & Workout
                                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.fine)) {
                                        val sleepH = summary.sleepDurationMinutes / 60
                                        val sleepM = summary.sleepDurationMinutes % 60
                                        Text(
                                            text = "Sleep: ${sleepH}h ${sleepM}m",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SleepAccent
                                        )
                                        Text(
                                            text = if (summary.workoutCompleted) "Workout: ${summary.workoutType ?: "Yes"}" else "No workout",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
