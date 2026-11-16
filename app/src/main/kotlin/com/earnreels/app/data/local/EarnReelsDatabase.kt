package com.earnreels.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.earnreels.app.data.local.dao.DailyHabitSummaryDao
import com.earnreels.app.data.local.dao.FocusSessionDao
import com.earnreels.app.data.local.dao.ReelsAllowanceDao
import com.earnreels.app.data.local.dao.RewardTransactionDao
import com.earnreels.app.data.local.dao.StreakDao
import com.earnreels.app.data.local.dao.WorkoutLogDao
import com.earnreels.app.data.local.entity.DailyHabitSummaryEntity
import com.earnreels.app.data.local.entity.FocusSessionEntity
import com.earnreels.app.data.local.entity.ReelsAllowanceEntity
import com.earnreels.app.data.local.entity.RewardTransactionEntity
import com.earnreels.app.data.local.entity.StreakEntity
import com.earnreels.app.data.local.entity.WorkoutLogEntity

@Database(
    entities = [
        FocusSessionEntity::class,
        DailyHabitSummaryEntity::class,
        WorkoutLogEntity::class,
        ReelsAllowanceEntity::class,
        RewardTransactionEntity::class,
        StreakEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EarnReelsDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun dailyHabitSummaryDao(): DailyHabitSummaryDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun reelsAllowanceDao(): ReelsAllowanceDao
    abstract fun rewardTransactionDao(): RewardTransactionDao
    abstract fun streakDao(): StreakDao

    companion object {
        @Volatile
        private var INSTANCE: EarnReelsDatabase? = null

        fun getInstance(context: Context): EarnReelsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EarnReelsDatabase::class.java,
                    "earnreels_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
