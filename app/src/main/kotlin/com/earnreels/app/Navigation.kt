package com.earnreels.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.earnreels.app.data.local.EarnReelsDatabase
import com.earnreels.app.data.repository.FocusRepository
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.data.repository.StreakRepository
import com.earnreels.app.features.focus.FocusScreen
import com.earnreels.app.features.focus.FocusViewModel
import com.earnreels.app.features.history.HistoryScreen
import com.earnreels.app.features.history.HistoryViewModel
import com.earnreels.app.features.rewards.RewardsScreen
import com.earnreels.app.features.rewards.RewardsViewModel
import com.earnreels.app.features.settings.SettingsScreen
import com.earnreels.app.features.settings.SettingsViewModel
import com.earnreels.app.features.today.TodayScreen
import com.earnreels.app.features.today.TodayViewModel
import com.earnreels.app.ui.components.NavTab

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(TodayNavKey)

    // Remember shared ViewModels
    val todayViewModel = remember(context) { TodayViewModel.create(context) }
    val rewardsViewModel = remember(context) { RewardsViewModel.create(context) }
    val historyViewModel = remember(context) { HistoryViewModel.create(context) }
    val settingsViewModel = remember(context) { SettingsViewModel.create(context) }
    val focusViewModel = remember(context) {
        val db = EarnReelsDatabase.getInstance(context)
        val focusRepo = FocusRepository(db.focusSessionDao())
        val rewardsRepo = RewardsRepository(db.reelsAllowanceDao(), db.rewardTransactionDao())
        val streakRepo = StreakRepository(db.streakDao(), rewardsRepo)
        val goalRepo = com.earnreels.app.data.repository.StudyGoalRepository(db.studyGoalDao())
        FocusViewModel(focusRepo, streakRepo, goalRepo)
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<TodayNavKey> {
                TodayScreen(
                    viewModel = todayViewModel,
                    onStartFocus = { backStack.add(FocusNavKey) },
                    onOpenSettings = { backStack.add(SettingsNavKey) },
                    onSelectTab = { tab ->
                        when (tab) {
                            NavTab.TODAY -> {}
                            NavTab.REWARDS -> backStack.add(RewardsNavKey)
                            NavTab.HISTORY -> backStack.add(HistoryNavKey)
                        }
                    },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }

            entry<RewardsNavKey> {
                RewardsScreen(
                    viewModel = rewardsViewModel,
                    onSelectTab = { tab ->
                        when (tab) {
                            NavTab.TODAY -> backStack.removeLastOrNull()
                            NavTab.REWARDS -> {}
                            NavTab.HISTORY -> {
                                backStack.removeLastOrNull()
                                backStack.add(HistoryNavKey)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }

            entry<HistoryNavKey> {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onSelectTab = { tab ->
                        when (tab) {
                            NavTab.TODAY -> backStack.removeLastOrNull()
                            NavTab.REWARDS -> {
                                backStack.removeLastOrNull()
                                backStack.add(RewardsNavKey)
                            }
                            NavTab.HISTORY -> {}
                        }
                    },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }

            entry<FocusNavKey> {
                FocusScreen(
                    viewModel = focusViewModel,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }

            entry<SettingsNavKey> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize().safeDrawingPadding()
                )
            }
        }
    )
}
