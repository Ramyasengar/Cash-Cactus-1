package com.example.cashcactus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Expense rows created when the user saves monthly expenses (dashboard flow). */
const val TRANSACTION_ORIGIN_MONTHLY = "monthly_snapshot"

@Entity(tableName = "transactions")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double = 0.0,

    val type: String = "", // "income" or "expense"

    val category: String = "", // food, rent, travel etc

    val date: Long = System.currentTimeMillis(), // IMPORTANT for AI

    /** "monthly_snapshot" for dashboard monthly save; empty for manual/other */
    val origin: String = ""
)