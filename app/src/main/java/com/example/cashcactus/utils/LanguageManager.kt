package com.example.cashcactus.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageManager {
    private const val SETTINGS_PREF = "app_settings"
    private const val KEY_SELECTED_LANGUAGE = "selected_language"

    const val ENGLISH = "en"
    const val HINDI = "hi"

    fun getSavedLanguage(context: Context): String {
        val pref = context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE)
        return pref.getString(KEY_SELECTED_LANGUAGE, ENGLISH) ?: ENGLISH
    }

    fun setLanguage(context: Context, languageCode: String) {
        context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED_LANGUAGE, languageCode)
            .apply()

        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun applySavedLanguage(context: Context) {
        setLanguage(context, getSavedLanguage(context))
    }

    /**
     * Creates a new Context with the given locale applied.
     * Used by Compose's CompositionLocalProvider to ensure all
     * stringResource() calls across the entire app use the correct locale.
     */
    fun createLocalizedContext(baseContext: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)
        return baseContext.createConfigurationContext(config)
    }
}
