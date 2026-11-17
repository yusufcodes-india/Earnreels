package com.earnreels.app.domain.model

data class StudyGoal(
    val id: Long = 0,
    val title: String,
    val targetMinutes: Int,
    val completedMinutes: Int = 0,
    val date: String, // YYYY-MM-DD
    val category: StudyGoalCategory = StudyGoalCategory.STUDY,
    val isCompleted: Boolean = false
) {
    val remainingMinutes: Int
        get() = (targetMinutes - completedMinutes).coerceAtLeast(0)

    val progress: Float
        get() = if (targetMinutes > 0) (completedMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f) else 0f
}

enum class StudyGoalCategory(val displayName: String, val iconEmoji: String) {
    LECTURE("Lecture", "🎓"),
    STUDY("Study", "📚"),
    LESSON("Lesson", "📖"),
    ASSIGNMENT("Assignment", "✍️"),
    REVISION("Revision", "🔄"),
    OTHER("Other", "🎯")
}
