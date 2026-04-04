package com.example.cashcactus.ui.screens

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.data.model.Transaction
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.viewmodel.MainViewModel
import com.example.cashcactus.viewmodel.TransactionViewModel

@Composable
fun ExpenseGraphScreen(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel()
    val transactions by transactionViewModel.allTransactions.collectAsState(initial = emptyList())

    val period = getAnalyticsPeriod(context)
    val filteredTransactions = transactions.filterByPeriod(period?.startDateMillis, period?.endDateMillis)

    val expenseTransactions = filteredTransactions.filter { it.type.equals("expense", ignoreCase = true) }
    val incomeTransactions = filteredTransactions.filter { it.type.equals("income", ignoreCase = true) }

    val budgetLimit = getBudgetLimit(context)
    val totalExpenses = expenseTransactions.sumOf { it.amount }
    val totalIncome = incomeTransactions.sumOf { it.amount }
    val remainingBudget = budgetLimit - totalExpenses
    val savings = totalIncome - totalExpenses

    val graphData = expenseTransactions
        .groupBy { if (it.category.isBlank()) "Other" else it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount }.toFloat() }
        .toList()
        .sortedByDescending { it.second }
    val aiGraphData = buildOptimizedExpenseMap(graphData.toMap())
        .toList()
        .sortedByDescending { it.second }
    val selectedGraphPreference = getGraphPreference(context)
    val selectedGraphData = when (selectedGraphPreference) {
        GRAPH_AI -> aiGraphData
        GRAPH_USER -> graphData
        else -> emptyList()
    }

    val maxValue = selectedGraphData.maxOfOrNull { it.second } ?: 1f

    CashCactusScreenScaffold(title = stringResource(R.string.expense_analysis)) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Budget Limit: ₹${budgetLimit.toInt()}", style = MaterialTheme.typography.titleMedium)
                    Text("Remaining Budget: ₹${remainingBudget.toInt()}", style = MaterialTheme.typography.titleMedium)
                    Text("Savings: ₹${savings.toInt()}", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (selectedGraphData.isEmpty()) {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.select_plan_from_dashboard),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                GraphCard(
                    data = selectedGraphData,
                    animatedValues = selectedGraphData.map { animateFloatAsState(it.second).value },
                    maxValue = maxValue,
                    barColor = if (selectedGraphPreference == GRAPH_AI) Color(0xFF26A69A) else Color(0xFF7E57C2)
                )
            }
        }
    }
}

private fun List<Transaction>.filterByPeriod(startDateMillis: Long?, endDateMillis: Long?): List<Transaction> {
    if (startDateMillis == null || endDateMillis == null) return this
    val inclusiveEnd = endDateMillis + 86_399_999L
    return filter { it.date in startDateMillis..inclusiveEnd }
}

@Composable
fun GraphCard(
    data: List<Pair<String, Float>>,
    animatedValues: List<Float>,
    maxValue: Float,
    barColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        if (data.isEmpty()) {
            Text(
                text = "No transactions found for selected period",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            return@Card
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(10.dp)
        ) {
            val barSpacing = size.width / data.size
            val barWidth = barSpacing * 0.5f

            val paint = Paint().apply {
                textAlign = Paint.Align.CENTER
                textSize = 22f
                color = android.graphics.Color.WHITE
            }

            data.forEachIndexed { index, pair ->
                val value = animatedValues[index]
                val barHeight = (value / maxValue) * (size.height - 60)
                val x = index * barSpacing + (barSpacing - barWidth) / 2

                drawRect(
                    color = barColor,
                    topLeft = Offset(x, size.height - barHeight - 30),
                    size = Size(barWidth, barHeight)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    value.toInt().toString(),
                    x + barWidth / 2,
                    size.height - barHeight - 35,
                    paint
                )

                drawContext.canvas.nativeCanvas.drawText(
                    pair.first,
                    x + barWidth / 2,
                    size.height - 5,
                    paint
                )
            }
        }
    }
}
