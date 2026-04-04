package com.example.cashcactus.utils

import android.content.Context

object QuizPreference {

    private const val PREF_NAME = "quiz_pref"
    private const val KEY_RESULT = "quiz_result"

    fun saveResult(context: Context, result: String) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putString(KEY_RESULT, result).apply()
    }

    fun getResult(context: Context): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_RESULT, null)
    }
}