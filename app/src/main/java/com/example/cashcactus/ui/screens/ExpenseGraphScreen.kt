package com.example.cashcactus.ui.screens

import android.graphics.Paint
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.viewmodel.MainViewModel

@Composable
fun ExpenseGraphScreen(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val graphType = getGraphPreference(context)

    val userData = listOf(
        "Food" to viewModel.food.toFloat(),
        "Rent" to viewModel.rent.toFloat(),
        "Medical" to viewModel.medical.toFloat(),
        "EMI" to viewModel.emi.toFloat(),
        "Other" to viewModel.additional.toFloat(),
        "Edu" to viewModel.education.toFloat()
    )

    val aiData = viewModel.getAIPredictedExpenses()
    val maxValue = (userData + aiData).maxOfOrNull { it.second } ?: 1f

    CashCactusScreenScaffold(title = stringResource(R.string.expense_analysis)) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("AI Insights", style = MaterialTheme.typography.titleMedium)
                    Text("Savings: ${viewModel.savingsPercent}%")
                    Text("Trend: ${viewModel.trend}")
                    Text("Prediction: ₹${viewModel.prediction.toInt()}")
                }
            }

            when (graphType) {
                "USER" -> {
                    Text("Your Expenses", style = MaterialTheme.typography.titleMedium)
                    GraphCard(
                        data = userData,
                        animatedValues = userData.map { animateFloatAsState(it.second).value },
                        maxValue = maxValue,
                        barColor = Color(0xFF7E57C2)
                    )
                }

                "AI" -> {
                    Text("AI Optimized", style = MaterialTheme.typography.titleMedium)
                    GraphCard(
                        data = aiData,
                        animatedValues = aiData.map { animateFloatAsState(it.second).value },
                        maxValue = maxValue,
                        barColor = Color(0xFF26A69A)
                    )
                }

                else -> {
                    Text("Your Expenses", style = MaterialTheme.typography.titleMedium)
                    GraphCard(
                        data = userData,
                        animatedValues = userData.map { animateFloatAsState(it.second).value },
                        maxValue = maxValue,
                        barColor = Color(0xFF7E57C2)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("AI Optimized", style = MaterialTheme.typography.titleMedium)
                    GraphCard(
                        data = aiData,
                        animatedValues = aiData.map { animateFloatAsState(it.second).value },
                        maxValue = maxValue,
                        barColor = Color(0xFF26A69A)
                    )

                    Text("Which analysis do you prefer?")
                    Button(
                        onClick = {
                            saveGraphPreference(context, "USER")
                            Toast.makeText(context, "User Graph Selected", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Use My Graph")
                    }
                    Button(
                        onClick = {
                            saveGraphPreference(context, "AI")
                            Toast.makeText(context, "AI Graph Selected", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Use AI Graph")
                    }
                }
            }

            Button(
                onClick = {
                    saveGraphPreference(context, "NONE")
                    Toast.makeText(context, "Preference Reset", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Preference")
            }

            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
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
