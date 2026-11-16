package com.earnreels.app.domain.usecase

import com.earnreels.app.data.repository.RewardsRepository

/**
 * Resets the Reels allowance at midnight.
 * Earned-but-unused allowance expires at midnight and does not carry over.
 */
class ResetDailyAllowanceUseCase(private val rewardsRepository: RewardsRepository) {

    suspend operator fun invoke(newDate: String, maxDailyCap: Int = 30) {
        rewardsRepository.resetDailyAllowance(newDate, maxDailyCap)
    }
}
