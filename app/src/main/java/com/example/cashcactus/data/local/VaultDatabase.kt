package com.example.cashcactus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashcactus.data.model.VaultData

@Database(entities = [VaultData::class], version = 1)
abstract class VaultDatabase : RoomDatabase() {

    abstract fun vaultDao(): VaultDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getDatabase(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "vault_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}