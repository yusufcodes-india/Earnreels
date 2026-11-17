package com.earnreels.app.ui.theme

import androidx.compose.ui.graphics.Color

// EarnReels "Calm Graphite" Design Palette
val GraphiteBackground = Color(0xFF101113)     // Background: deep calm graphite (#101113)
val CardSurface = Color(0xFF17191C)            // Card surface: flat dark graphite (#17191C)
val CardSurfaceVariant = Color(0xFF1E2124)     // Elevated surface: soft graphite (#1E2124)
val BorderSubtle = Color(0xFF26292D)           // Border / divider: muted tone (#26292D)

val PrimaryText = Color(0xFFEDEDED)            // Primary text: soft off-white (#EDEDED)
val SecondaryText = Color(0xFFA1A6AB)          // Secondary text: muted gray (#A1A6AB)

val ProgressAccent = Color(0xFF8AB4F8)         // Progress accent: calm soft blue (#8AB4F8)
val SuccessAccent = Color(0xFF7FB89A)          // Success / completed: muted sage green (#7FB89A)
val SleepAccent = Color(0xFF9A8FBF)            // Sleep accent: soft muted lavender (#9A8FBF)
val BlockedRed = Color(0xFFD98B80)             // Blocked state / error: muted terracotta (#D98B80)

// Aliases for seamless backward compatibility across existing files
val CharcoalBackground = GraphiteBackground
val MatteBlackBackground = GraphiteBackground
val TextMain = PrimaryText
val TextSecondary = SecondaryText
val LimeAccent = ProgressAccent                // Maps existing Lime references to calm ProgressAccent
val ErrorRed = BlockedRed
val AccentWhite = PrimaryText

// Light-fallback support (maintaining clean contrast)
val LightBackground = Color(0xFFF6F8FA)
val LightSurface = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFD8DEE4)
val LightTextMain = Color(0xFF1F2328)
val LightTextSecondary = Color(0xFF656D76)
val LightLimeAccent = ProgressAccent
