package com.earnreels.app.blocking

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.earnreels.app.MainActivity
import com.earnreels.app.ui.components.PrimaryButton
import com.earnreels.app.ui.components.RewardCard
import com.earnreels.app.ui.theme.CharcoalBackground
import com.earnreels.app.ui.theme.EarnReelsTheme
import com.earnreels.app.ui.theme.ErrorRed
import com.earnreels.app.ui.theme.Spacing
import com.earnreels.app.ui.theme.TextMain
import com.earnreels.app.ui.theme.TextSecondary

class BlockerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EarnReelsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CharcoalBackground
                ) {
                    BlockerContent(
                        onReturnToApp = {
                            val intent = Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                            startActivity(intent)
                            finish()
                        },
                        onGoHome = {
                            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(homeIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BlockerContent(
    onReturnToApp: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .padding(horizontal = Spacing.screenMargin, vertical = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Icon & Notice
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.HourglassBottom,
                    contentDescription = "Allowance exhausted",
                    tint = ErrorRed,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(modifier = Modifier.height(Spacing.base))
            Text(
                text = "No Reels time left",
                style = MaterialTheme.typography.titleLarge,
                color = TextMain
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "You've used up your daily allowance. Complete a healthy habit in EarnReels to recharge your wallet.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Spacing.base)
            )
        }

        // Suggested next reward
        RewardCard(
            rewardTitle = "Next Action to Earn Time",
            rewardDescription = "Complete a 25-minute Pomodoro focus session",
            rewardAmountMinutes = 5
        )

        // Bottom Actions
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                text = "Return to EarnReels",
                onClick = onReturnToApp
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            TextButton(
                onClick = onGoHome,
                modifier = Modifier.height(Spacing.buttonHeight)
            ) {
                Text(
                    text = "Go to Phone Home Screen",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
