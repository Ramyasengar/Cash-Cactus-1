package com.example.cashcactus.ui.screens

import android.content.Context

data class AnalyticsPeriod(
    val startDateMillis: Long,
    val endDateMillis: Long
)

private const val ANALYTICS_PREFS = "analytics_pref"
private const val KEY_START_DATE = "start_date"
private const val KEY_END_DATE = "end_date"
private const val KEY_BUDGET_LIMIT = "budget_limit"

fun saveAnalyticsPeriod(context: Context, startDateMillis: Long, endDateMillis: Long) {
    context.getSharedPreferences(ANALYTICS_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putLong(KEY_START_DATE, startDateMillis)
        .putLong(KEY_END_DATE, endDateMillis)
        .apply()
}

fun getAnalyticsPeriod(context: Context): AnalyticsPeriod? {
    val prefs = context.getSharedPreferences(ANALYTICS_PREFS, Context.MODE_PRIVATE)
    val start = prefs.getLong(KEY_START_DATE, -1L)
    val end = prefs.getLong(KEY_END_DATE, -1L)

    if (start <= 0L || end <= 0L) return null

    return AnalyticsPeriod(startDateMillis = start, endDateMillis = end)
}

fun saveBudgetLimit(context: Context, budgetLimit: Double) {
    context.getSharedPreferences(ANALYTICS_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putFloat(KEY_BUDGET_LIMIT, budgetLimit.toFloat())
        .apply()
}

fun getBudgetLimit(context: Context): Double {
    val prefs = context.getSharedPreferences(ANALYTICS_PREFS, Context.MODE_PRIVATE)
    return prefs.getFloat(KEY_BUDGET_LIMIT, 0f).toDouble()
}

fun clearAnalyticsPreferences(context: Context) {
    context.getSharedPreferences(ANALYTICS_PREFS, Context.MODE_PRIVATE).edit().clear().apply()
}
