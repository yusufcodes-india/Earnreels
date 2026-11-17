package com.earnreels.app.ui.theme

import androidx.compose.ui.graphics.Color

// EarnReels Deep Matte OLED Minimalist Palette
val MatteBlackBackground = Color(0xFF0A0A0A)    // Deep matte black calm on OLED screens without glare
val CharcoalBackground = MatteBlackBackground   // Unified background reference
val CardSurface = Color(0xFF161616)            // Subtle dark charcoal for cards and surfaces
val CardSurfaceVariant = Color(0xFF1E1E1E)     // Elevated surface variant
val BorderSubtle = Color(0xFF282828)           // Subtle border separation

val AccentWhite = Color(0xFFFFFFFF)            // Pure white for active block switches & highlights
val LimeAccent = AccentWhite                   // Backward-compatibility alias for active indicators
val SleepAccent = Color(0xFF8E8E93)            // Muted neutral gray accent
val TextMain = Color(0xFFF2F2F2)               // Soft off-white for crisp readability
val TextSecondary = Color(0xFF8E8E93)          // Muted neutral gray for timestamps and stats
val ErrorRed = Color(0xFFFF453A)               // OLED soft red for blocked state / errors

// Light-fallback support (maintaining clean contrast)
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFE2E8F0)
val LightTextMain = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
val LightLimeAccent = Color(0xFF0A0A0A)
