package com.example.cashcactus

import android.content.Context
import android.net.Uri

fun readTransactionSms(context: Context): List<Float> {

    val smsList = mutableListOf<Float>()

    val cursor = context.contentResolver.query(
        Uri.parse("content://sms/inbox"),
        null,
        null,
        null,
        null
    )

    cursor?.use {

        val bodyIndex = it.getColumnIndex("body")

        while (it.moveToNext()) {

            val message = it.getString(bodyIndex)

            // 🔍 Simple detection
            if (message.contains("debited", true) ||
                message.contains("spent", true) ||
                message.contains("paid", true)
            ) {

                val amount = extractAmount(message)

                if (amount != null) {
                    smsList.add(amount)
                }
            }
        }
    }

    return smsList
}
fun extractAmount(message: String): Float? {

    val regex = Regex("(\\d+[,.]?\\d*)")

    val match = regex.find(message)

    return match?.value?.replace(",", "")?.toFloatOrNull()
}