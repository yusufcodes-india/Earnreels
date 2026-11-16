package com.earnreels.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val EarnReelsShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),       // 20-24dp for cards
    extraLarge = RoundedCornerShape(28.dp)
)

val CardShape = RoundedCornerShape(22.dp)
val ButtonShape = RoundedCornerShape(16.dp)
val PillShape = RoundedCornerShape(50)
