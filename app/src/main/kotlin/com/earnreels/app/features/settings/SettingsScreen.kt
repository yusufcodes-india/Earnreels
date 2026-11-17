package com.earnreels.app.features.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.earnreels.app.health.HealthConnectStatus
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.CardShape
import com.earnreels.app.ui.theme.CardSurface
import com.earnreels.app.ui.theme.CharcoalBackground
import com.earnreels.app.ui.theme.ErrorRed
import com.earnreels.app.ui.theme.LimeAccent
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.TextMain
import com.earnreels.app.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val healthConnectStatus by viewModel.healthConnectStatus.collectAsStateWithLifecycle()
    val hasHealthPermissions by viewModel.hasHealthPermissions.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .padding(horizontal = Spacing.screenMargin)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(Spacing.base))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = TextMain
                )
            }
            Spacer(modifier = Modifier.size(Spacing.sm))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                color = TextMain
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // SECTION: Goals & Limits
        SectionTitle("Goals & Limits")
        Spacer(modifier = Modifier.height(Spacing.sm))

        Card(
            shape = CardShape,
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Spacing.cardPadding)) {
                // Daily Step Goal
                SettingRow(
                    label = "Daily Step Goal",
                    value = "%,d steps".format(settings.dailyStepGoal),
                    onClick = {
                        val next = when (settings.dailyStepGoal) {
                            6000 -> 8000
                            8000 -> 10000
                            10000 -> 12000
                            else -> 6000
                        }
                        viewModel.updateDailyStepGoal(next)
                    }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Target Bedtime
                SettingRow(
                    label = "Target Bedtime",
                    value = settings.formattedBedtime,
                    onClick = {
                        val (h, m) = when (settings.targetBedtimeHour) {
                            22 -> Pair(22, 30)
                            23 -> Pair(0, 0)
                            0 -> Pair(22, 0)
                            else -> Pair(23, 0)
                        }
                        viewModel.updateTargetBedtime(h, m)
                    }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Min Sleep Duration
                SettingRow(
                    label = "Minimum Sleep Duration",
                    value = settings.formattedMinSleep,
                    onClick = {
                        val next = when (settings.minSleepDurationMinutes) {
                            360 -> 420 // 6h -> 7h
                            420 -> 480 // 7h -> 8h
                            else -> 360 // 8h -> 6h
                        }
                        viewModel.updateMinSleepDuration(next)
                    }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Daily Reels Allowance Cap
                SettingRow(
                    label = "Daily Allowance Cap",
                    value = "${settings.dailyReelsCap} minutes",
                    onClick = {
                        val next = when (settings.dailyReelsCap) {
                            15 -> 30
                            30 -> 45
                            45 -> 60
                            else -> 15
                        }
                        viewModel.updateDailyReelsCap(next)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // SECTION: Reels/Shorts Blocking
        SectionTitle("Reels & Shorts Blocker")
        Spacer(modifier = Modifier.height(Spacing.sm))

        Card(
            shape = CardShape,
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Spacing.cardPadding)) {
                // Enable Blocking Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Blocker",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextMain
                        )
                        Text(
                            text = "Intercepts Reels and Shorts when allowance is 0",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = settings.blockingEnabled,
                        onCheckedChange = { viewModel.setBlockingEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CharcoalBackground,
                            checkedTrackColor = LimeAccent,
                            checkedBorderColor = LimeAccent,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = BorderSubtle,
                            uncheckedBorderColor = BorderSubtle
                        )
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                // Strict Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Strict Mode",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextMain
                        )
                        Text(
                            text = "Block entire Instagram/YouTube app instead of only Reels/Shorts surfaces",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = settings.strictModeAppBlocking,
                        onCheckedChange = { viewModel.setStrictMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CharcoalBackground,
                            checkedTrackColor = LimeAccent,
                            checkedBorderColor = LimeAccent,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = BorderSubtle,
                            uncheckedBorderColor = BorderSubtle
                        )
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                // Accessibility Settings Shortcut
                SettingRow(
                    label = "Accessibility Permission",
                    value = "Open Android Settings",
                    onClick = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // SECTION: Health Connect & Privacy
        SectionTitle("Privacy & Health Connect")
        Spacer(modifier = Modifier.height(Spacing.sm))

        Card(
            shape = CardShape,
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Spacing.cardPadding)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = LimeAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "100% On-Device & Offline",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextMain
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.fine))
                Text(
                    text = "EarnReels has no internet permission. Your habits, focus times, step counts, and sleep logs never leave your phone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                SettingRow(
                    label = "Health Connect Status",
                    value = when {
                        hasHealthPermissions -> "Connected & Active"
                        healthConnectStatus == HealthConnectStatus.AVAILABLE -> "Permissions Required"
                        healthConnectStatus == HealthConnectStatus.NOT_INSTALLED -> "Not Installed"
                        else -> "Unavailable"
                    },
                    onClick = {
                        // Open health connect settings or check permissions
                        viewModel.checkHealthPermissions()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // SECTION: Data Management
        SectionTitle("Data Management")
        Spacer(modifier = Modifier.height(Spacing.sm))

        Card(
            shape = CardShape,
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Spacing.cardPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResetDialog = true }
                        .padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reset All Data",
                            style = MaterialTheme.typography.titleSmall,
                            color = ErrorRed
                        )
                        Text(
                            text = "Clears all focus sessions, allowances, and habit history",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "Warning",
                        tint = ErrorRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }

    // Confirmation Dialog for Data Reset
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = CardSurface,
            title = {
                Text(
                    text = "Reset All App Data?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMain
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all your focus sessions, habit history, and reset your Reels Wallet. This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset Everything", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = LimeAccent
    )
}

@Composable
fun SettingRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMain
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = LimeAccent
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
