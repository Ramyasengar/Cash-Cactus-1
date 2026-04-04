package com.example.cashcactus.utils

import android.util.Base64

object SimpleCrypto {

    fun encrypt(data: String): String {
        return Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)
    }

    fun decrypt(data: String): String {
        return String(Base64.decode(data, Base64.DEFAULT))
    }
}