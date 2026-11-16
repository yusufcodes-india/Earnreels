package com.earnreels.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.earnreels.app.domain.model.FocusSession

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val durationMinutes: Int,
    val startedAt: Long,
    val completedAt: Long? = null,
    val isCompleted: Boolean = false,
    val earnedReelsMinutes: Int = 0
) {
    fun toDomain() = FocusSession(
        id = id,
        durationMinutes = durationMinutes,
        startedAt = startedAt,
        completedAt = completedAt,
        isCompleted = isCompleted,
        earnedReelsMinutes = earnedReelsMinutes
    )

    companion object {
        fun fromDomain(model: FocusSession, date: String) = FocusSessionEntity(
            id = model.id,
            date = date,
            durationMinutes = model.durationMinutes,
            startedAt = model.startedAt,
            completedAt = model.completedAt,
            isCompleted = model.isCompleted,
            earnedReelsMinutes = model.earnedReelsMinutes
        )
    }
}
