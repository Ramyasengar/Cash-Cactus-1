package com.example.cashcactus.utils

import android.content.Context

object PrivacyHelper {

    private const val PREF_NAME = "privacy_prefs"
    private const val KEY_ACCEPTED = "privacy_accepted"

    fun isAccepted(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ACCEPTED, false)
    }

    fun setAccepted(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ACCEPTED, true)
            .apply()
    }
}