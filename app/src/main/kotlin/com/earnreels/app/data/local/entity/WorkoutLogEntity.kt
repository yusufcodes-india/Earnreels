package com.earnreels.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.earnreels.app.domain.model.WorkoutLog
import com.earnreels.app.domain.model.WorkoutType

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val workoutType: String,
    val completedAt: Long,
    val earnedMinutes: Int
) {
    fun toDomain() = WorkoutLog(
        id = id,
        date = date,
        workoutType = try {
            WorkoutType.valueOf(workoutType)
        } catch (e: Exception) {
            WorkoutType.OTHER
        },
        completedAt = completedAt,
        earnedMinutes = earnedMinutes
    )

    companion object {
        fun fromDomain(model: WorkoutLog) = WorkoutLogEntity(
            id = model.id,
            date = model.date,
            workoutType = model.workoutType.name,
            completedAt = model.completedAt,
            earnedMinutes = model.earnedMinutes
        )
    }
}
