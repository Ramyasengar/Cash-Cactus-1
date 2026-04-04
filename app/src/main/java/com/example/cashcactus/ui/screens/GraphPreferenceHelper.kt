package com.example.cashcactus.ui.screens

import android.content.Context

fun saveGraphPreference(context: Context, type: String) {
    val prefs = context.getSharedPreferences("graph_pref", Context.MODE_PRIVATE)
    prefs.edit().putString("graph_type", type).apply()
}

fun getGraphPreference(context: Context): String {
    val prefs = context.getSharedPreferences("graph_pref", Context.MODE_PRIVATE)
    return prefs.getString("graph_type", "NONE") ?: "NONE"
}