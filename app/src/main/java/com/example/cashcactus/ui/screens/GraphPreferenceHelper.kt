package com.example.cashcactus.ui.screens

import android.content.Context

const val GRAPH_NONE = "NONE"
const val GRAPH_USER = "USER"
const val GRAPH_AI = "AI"

fun saveGraphPreference(context: Context, type: String) {
    val prefs = context.getSharedPreferences("graph_pref", Context.MODE_PRIVATE)
    prefs.edit().putString("graph_type", type).apply()
}

fun getGraphPreference(context: Context): String {
    val prefs = context.getSharedPreferences("graph_pref", Context.MODE_PRIVATE)
    return prefs.getString("graph_type", GRAPH_NONE) ?: GRAPH_NONE
}

fun buildOptimizedExpenseMap(actualExpenses: Map<String, Float>): Map<String, Float> {
    return actualExpenses.mapValues { (category, amount) ->
        if (category.equals("rent", ignoreCase = true)) {
            amount
        } else {
            amount * 0.85f
        }
    }
}
