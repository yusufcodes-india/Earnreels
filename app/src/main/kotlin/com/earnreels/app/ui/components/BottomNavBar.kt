package com.earnreels.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.earnreels.app.ui.theme.BorderSubtle
import com.earnreels.app.ui.theme.CardSurface
import com.earnreels.app.ui.theme.CharcoalBackground
import com.earnreels.app.ui.theme.LimeAccent
import com.earnreels.app.ui.theme.TextSecondary

enum class NavTab(val title: String, val icon: ImageVector) {
    TODAY("Today", Icons.Rounded.Home),
    REWARDS("Rewards", Icons.Rounded.Star),
    HISTORY("History", Icons.Rounded.DateRange)
}

@Composable
fun BottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = CardSurface,
        tonalElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
    ) {
        NavTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CharcoalBackground,
                    selectedTextColor = LimeAccent,
                    indicatorColor = LimeAccent,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
