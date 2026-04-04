package com.example.cashcactus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashcactus.data.model.*

@Database(
    entities = [
        User::class,
        Expense::class,
        DashboardData::class,
        Transaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao   // ✅ ADD THIS

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cash_cactus_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}