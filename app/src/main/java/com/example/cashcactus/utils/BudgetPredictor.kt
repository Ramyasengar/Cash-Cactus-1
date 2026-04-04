package com.example.cashcactus.utils

import com.example.cashcactus.data.model.Transaction
import kotlin.math.roundToInt

object BudgetPredictor {

    fun predictMonthlySpending(transactions: List<Transaction>): Double {
        val expenses = transactions.filter { it.type == "expense" }

        if (expenses.isEmpty()) return 0.0

        val total = expenses.sumOf { it.amount }

        val days = expenses.map { it.date }
            .distinct()
            .size

        val dailyAvg = if (days == 0) 0.0 else total / days

        return dailyAvg * 30 // monthly prediction
    }

    fun generateInsight(transactions: List<Transaction>): String {
        val predicted = predictMonthlySpending(transactions)

        return when {
            predicted > 20000 -> "⚠️ You may overspend this month"
            predicted > 10000 -> "⚡ Keep an eye on your spending"
            else -> "✅ Great! You're managing your budget well"
        }
    }
    fun getCategoryBreakdown(transactions: List<Transaction>): Map<String, Double> {
        val expenses = transactions.filter { it.type == "expense" }

        return expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }
    fun getSmartAlerts(transactions: List<Transaction>): List<String> {
        val alerts = mutableListOf<String>()

        val categoryMap = getCategoryBreakdown(transactions)

        categoryMap.forEach { (category, amount) ->
            if (amount > 5000) {
                alerts.add("⚠️ High spending on $category")
            }
        }

        if (transactions.count { it.type == "expense" } > 50) {
            alerts.add("📊 Too many transactions this month")
        }

        if (alerts.isEmpty()) {
            alerts.add("✅ Your spending looks balanced")
        }

        return alerts
    }
    fun calculateSavingsPercentage(transactions: List<Transaction>): Int {
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }

        if (income == 0.0) return 0

        val savings = income - expense
        return ((savings / income) * 100).toInt()
    }
    fun weeklyTrend(transactions: List<Transaction>): String {

        val sorted = transactions.sortedBy { it.date }

        if (sorted.size < 2) return "Not enough data"

        val mid = sorted.size / 2

        val firstHalf = sorted.take(mid).sumOf { it.amount }
        val secondHalf = sorted.takeLast(mid).sumOf { it.amount }

        return if (secondHalf > firstHalf) {
            "📈 Spending increased recently"
        } else {
            "📉 Spending decreased recently"
        }
    }
}