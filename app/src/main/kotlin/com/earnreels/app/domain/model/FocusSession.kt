package com.earnreels.app.domain.model

data class FocusSession(
    val id: Long = 0,
    val durationMinutes: Int,
    val startedAt: Long,
    val completedAt: Long? = null,
    val isCompleted: Boolean = false,
    val earnedReelsMinutes: Int = 0
) {
    companion object {
        val ALLOWED_DURATIONS = listOf(15, 25, 45, 60)
        const val DEFAULT_DURATION = 25
    }
}
