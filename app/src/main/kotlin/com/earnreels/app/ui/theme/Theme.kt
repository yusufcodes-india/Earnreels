package com.earnreels.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ProgressAccent,
    onPrimary = GraphiteBackground,
    primaryContainer = CardSurfaceVariant,
    onPrimaryContainer = PrimaryText,
    secondary = SleepAccent,
    onSecondary = GraphiteBackground,
    background = GraphiteBackground,
    onBackground = PrimaryText,
    surface = CardSurface,
    onSurface = PrimaryText,
    surfaceVariant = CardSurfaceVariant,
    onSurfaceVariant = SecondaryText,
    outline = BorderSubtle,
    error = BlockedRed,
    onError = GraphiteBackground
)

private val LightColorScheme = lightColorScheme(
    primary = LightLimeAccent,
    onPrimary = LightSurface,
    primaryContainer = LightSurface,
    onPrimaryContainer = LightLimeAccent,
    secondary = SleepAccent,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = LightTextMain,
    surface = LightSurface,
    onSurface = LightTextMain,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = ErrorRed,
    onError = LightSurface
)

@Composable
fun EarnReelsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // EarnReels is dark-first by design, but gracefully supports light mode
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EarnReelsTypography,
        shapes = EarnReelsShapes,
        content = content
    )
}
