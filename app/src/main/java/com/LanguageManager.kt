package com.example.cashcactus

import android.content.Context
import java.util.Locale

object LanguageManager {

    fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("lang", language).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("lang", "en") ?: "en"
    }

    fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}