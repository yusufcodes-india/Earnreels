package com.earnreels.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.LimeAccent
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.TextSecondary

@Composable
fun ReelsWalletRing(
    remainingMinutes: Int,
    maxMinutes: Int = 30,
    modifier: Modifier = Modifier,
    size: Dp = 210.dp,
    strokeWidth: Dp = 14.dp
) {
    val progressTarget = if (maxMinutes > 0) {
        (remainingMinutes.toFloat() / maxMinutes.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "wallet_ring_progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()

            // Background subtle track
            drawCircle(
                color = BorderSubtle,
                style = Stroke(width = stroke)
            )

            // Active Lime progress arc
            drawArc(
                color = LimeAccent,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$remainingMinutes",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(Spacing.fine))
            Text(
                text = "MINUTES LEFT",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}
