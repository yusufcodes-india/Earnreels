package com.earnreels.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.earnreels.app.domain.model.StudyGoal
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.CardShape
import com.earnreels.app.ui.theme.CardSurface
import com.earnreels.app.ui.theme.CardSurfaceVariant
import com.earnreels.app.ui.theme.CharcoalBackground
import com.earnreels.app.ui.theme.PillShape
import com.earnreels.app.ui.theme.PrimaryText
import com.earnreels.app.ui.theme.ProgressAccent
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.SuccessAccent
import com.earnreels.app.ui.theme.TextMain
import com.earnreels.app.ui.theme.TextSecondary

@Composable
fun StudyGoalCard(
    goal: StudyGoal,
    onStartFocus: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.cardPadding)
        ) {
            // Header: Category emoji, title & Delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = goal.category.iconEmoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextMain,
                            maxLines = 1
                        )
                        Text(
                            text = goal.category.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete Goal",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Metrics row: completed / target and remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${goal.completedMinutes} / ${goal.targetMinutes} min",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMain
                )
                Text(
                    text = if (goal.isCompleted) "Completed" else "${goal.remainingMinutes}m remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (goal.isCompleted) SuccessAccent else TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Progress bar (thin 4dp)
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = ProgressAccent,
                trackColor = BorderSubtle,
                strokeCap = StrokeCap.Round
            )

            // Quick Focus Action (if not completed)
            if (!goal.isCompleted) {
                Spacer(modifier = Modifier.height(Spacing.md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .background(CardSurfaceVariant, PillShape)
                            .border(1.dp, ProgressAccent, PillShape)
                            .clickable { onStartFocus() }
                            .padding(horizontal = Spacing.md, vertical = Spacing.fine),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = ProgressAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Focus on this",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryText
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = SuccessAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Goal achieved! Reels allowance awarded.",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessAccent
                    )
                }
            }
        }
    }
}
