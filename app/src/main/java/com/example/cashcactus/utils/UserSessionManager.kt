package com.example.cashcactus.utils

import android.content.Context
import com.example.cashcactus.data.model.User

object UserSessionManager {

    private const val PREF_NAME = "user_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAME = "name"

    fun saveSession(context: Context, user: User) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_USER_ID, user.id)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_NAME, user.name)
            .apply()
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_USER_ID, 0) > 0
    }

    fun getUserId(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_USER_ID, 0)
    }
}
