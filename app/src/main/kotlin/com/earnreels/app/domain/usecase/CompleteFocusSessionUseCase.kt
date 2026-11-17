package com.earnreels.app.domain.usecase

import com.earnreels.app.data.repository.FocusRepository
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.data.repository.StreakRepository
import com.earnreels.app.data.repository.StudyGoalRepository
import com.earnreels.app.domain.model.HabitRewardType
import java.time.LocalDate

class CompleteFocusSessionUseCase(
    private val focusRepository: FocusRepository,
    private val rewardsRepository: RewardsRepository,
    private val streakRepository: StreakRepository,
    private val studyGoalRepository: StudyGoalRepository? = null,
    private val calculateRewardUseCase: CalculateRewardUseCase = CalculateRewardUseCase()
) {

    suspend operator fun invoke(
        sessionId: Long,
        durationMinutes: Int,
        linkedGoalId: Long? = null
    ): Int {
        val today = LocalDate.now().toString()
        val rewardAmount = calculateRewardUseCase.forFocusSession(durationMinutes)

        val awardedMinutes = rewardsRepository.awardReward(
            date = today,
            habitType = HabitRewardType.fromFocusDuration(durationMinutes),
            rewardMinutes = rewardAmount,
            description = "Completed $durationMinutes-min focus session",
            allowMultipleSameDay = true // Focus sessions can be completed multiple times up to daily cap
        )

        focusRepository.completeSession(sessionId, awardedMinutes)
        streakRepository.recordHabitCompletion(StreakRepository.FOCUS, today)

        // If session was linked to a specific study/lesson goal, update goal progress!
        if (linkedGoalId != null && linkedGoalId > 0) {
            studyGoalRepository?.logProgress(linkedGoalId, durationMinutes)
        }

        return awardedMinutes
    }
}
