package com.earnreels.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.CardSurfaceVariant
import com.earnreels.app.ui.theme.LimeAccent
import com.earnreels.app.ui.theme.PillShape
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.TextMain

@Composable
fun StreakBadge(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.fine),
        modifier = modifier
            .background(CardSurfaceVariant, PillShape)
            .border(1.dp, BorderSubtle, PillShape)
            .padding(horizontal = Spacing.md, vertical = Spacing.fine)
    ) {
        Icon(
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = "Streak",
            tint = if (streakDays > 0) LimeAccent else BorderSubtle,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$streakDays d",
            style = MaterialTheme.typography.labelMedium,
            color = TextMain
        )
    }
}
