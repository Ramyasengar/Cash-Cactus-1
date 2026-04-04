package com.example.cashcactus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DashboardData(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,

    // BASIC INFO
    val category: String = "",
    val age: Int = 0,
    val salary: Float = 0f,
    val savingType: String = "",
    val savingValue: Float = 0f,

    // EXPENSES
    val totalBalance: Double = 0.0,
    val food: Double = 0.0,
    val rent: Double = 0.0,
    val medical: Double = 0.0,
    val emi: Double = 0.0,
    val education: Double = 0.0,
    val additional: Double = 0.0
)