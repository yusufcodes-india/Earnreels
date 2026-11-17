package com.earnreels.app.features.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earnreels.app.data.local.EarnReelsDatabase
import com.earnreels.app.data.repository.StepsRepository
import com.earnreels.app.domain.model.DailyHabitSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    private val db: EarnReelsDatabase
) : ViewModel() {

    val recentSummaries: StateFlow<List<DailyHabitSummary>> = db.dailyHabitSummaryDao().getRecentSummaries(7)
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    companion object {
        fun create(context: Context): HistoryViewModel {
            val db = EarnReelsDatabase.getInstance(context)
            return HistoryViewModel(db)
        }
    }
}
