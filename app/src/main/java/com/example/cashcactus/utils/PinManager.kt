package com.example.cashcactus.utils


import android.content.Context

object PinManager {

    private const val PREF = "vault_pin"
    private const val KEY_PIN = "user_pin"

    fun savePin(context: Context, pin: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PIN, PasswordSecurity.hash(pin))
            .apply()
    }

    fun getPin(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_PIN, null)
    }

    fun isPinSet(context: Context): Boolean {
        return getPin(context) != null
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val storedPin = getPin(context) ?: return false
        return PasswordSecurity.verify(pin, storedPin)
    }
}
