package com.example.cashcactus.utils

import android.content.Context

object VaultSessionManager {

    private const val PREF_NAME = "vault_prefs"
    private const val KEY_UNLOCK_TIME = "unlock_time"
    private const val TIMEOUT = 30000L // 30 sec

    // 🔓 Unlock
    fun unlock(context: Context) {
        val time = System.currentTimeMillis()

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_UNLOCK_TIME, time)
            .apply()
    }

    // 🔒 Lock (👉 ADD THIS HERE)
    fun lock(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_UNLOCK_TIME, 0L)
            .apply()
    }

    fun isUnlocked(context: Context): Boolean {
        val lastTime = context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_UNLOCK_TIME, 0L)

        // 🔴 If never unlocked → return false
        if (lastTime == 0L) return false

        val isValid = System.currentTimeMillis() - lastTime < TIMEOUT

        // 🔥 AUTO RESET if expired
        if (!isValid) {
            lock(context)
        }

        return isValid
    }
}