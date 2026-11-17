package com.earnreels.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.earnreels.app.domain.model.StudyGoal
import com.earnreels.app.domain.model.StudyGoalCategory

@Entity(tableName = "study_goals")
data class StudyGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetMinutes: Int,
    val completedMinutes: Int = 0,
    val date: String, // YYYY-MM-DD
    val category: String = StudyGoalCategory.STUDY.name,
    val isCompleted: Boolean = false
) {
    fun toDomain() = StudyGoal(
        id = id,
        title = title,
        targetMinutes = targetMinutes,
        completedMinutes = completedMinutes,
        date = date,
        category = try {
            StudyGoalCategory.valueOf(category)
        } catch (e: Exception) {
            StudyGoalCategory.STUDY
        },
        isCompleted = isCompleted
    )

    companion object {
        fun fromDomain(model: StudyGoal) = StudyGoalEntity(
            id = model.id,
            title = model.title,
            targetMinutes = model.targetMinutes,
            completedMinutes = model.completedMinutes,
            date = model.date,
            category = model.category.name,
            isCompleted = model.isCompleted
        )
    }
}
