package com.earnreels.app.domain.usecase

import com.earnreels.app.data.repository.RewardsRepository

/**
 * Deducts minutes while the user is actively browsing Reels or Shorts.
 * Returns true if allowance remains (> 0), false if allowance has hit zero (trigger blocker).
 */
class DeductAllowanceUseCase(private val rewardsRepository: RewardsRepository) {

    suspend operator fun invoke(date: String, minutesToDeduct: Int = 1): Boolean {
        return rewardsRepository.deductMinutes(date, minutesToDeduct)
    }
}
