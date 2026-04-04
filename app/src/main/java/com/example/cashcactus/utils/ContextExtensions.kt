package com.example.cashcactus.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

fun Context.findActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}