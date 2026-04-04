package com.example.cashcactus.utils

import android.content.Context

object EmergencyAccessManager {

    private const val PREF_NAME = "emergency_access"
    private const val KEY_PIN_HASH = "temp_pin_hash"
    private const val KEY_EXPIRES_AT = "expires_at"

    fun createTemporaryPin(
        context: Context,
        pin: String,
        durationMillis: Long
    ) {
        val expiresAt = System.currentTimeMillis() + durationMillis
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PIN_HASH, PasswordSecurity.hash(pin))
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    fun verifyTemporaryPin(context: Context, pin: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false

        if (expiresAt <= System.currentTimeMillis()) {
            clear(context)
            return false
        }

        return PasswordSecurity.verify(pin, storedHash)
    }

    fun getRemainingSeconds(context: Context): Int {
        val expiresAt = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_EXPIRES_AT, 0L)
        val remaining = ((expiresAt - System.currentTimeMillis()) / 1000).toInt()
        return remaining.coerceAtLeast(0)
    }

    fun hasActivePin(context: Context): Boolean = getRemainingSeconds(context) > 0

    fun clear(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
