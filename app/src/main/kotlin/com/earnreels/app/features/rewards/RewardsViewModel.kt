package com.earnreels.app.features.rewards

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earnreels.app.data.local.EarnReelsDatabase
import com.earnreels.app.data.preferences.UserPreferences
import com.earnreels.app.data.repository.RewardsRepository
import com.earnreels.app.domain.model.AppSettings
import com.earnreels.app.domain.model.ReelsAllowance
import com.earnreels.app.domain.model.RewardTransaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class RewardsViewModel(
    private val rewardsRepository: RewardsRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    private val today = LocalDate.now().toString()

    val allowance: StateFlow<ReelsAllowance> = rewardsRepository.getAllowanceForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReelsAllowance(date = today))

    val transactions: StateFlow<List<RewardTransaction>> = rewardsRepository.getTransactionsForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettings> = preferences.appSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    companion object {
        fun create(context: Context): RewardsViewModel {
            val db = EarnReelsDatabase.getInstance(context)
            val rewardsRepo = RewardsRepository(db.reelsAllowanceDao(), db.rewardTransactionDao())
            val prefs = UserPreferences(context)
            return RewardsViewModel(rewardsRepo, prefs)
        }
    }
}
