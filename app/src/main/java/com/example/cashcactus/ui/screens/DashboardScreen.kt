package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashcactus.data.model.Transaction
import com.example.cashcactus.R
import com.example.cashcactus.viewmodel.MainViewModel
import com.example.cashcactus.viewmodel.TransactionViewModel
import com.example.cashcactus.ui.components.PieChartView
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {

    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel()
    val transactions by transactionViewModel.allTransactions.collectAsState(initial = emptyList())

    val expenseMap = transactions
        .filter { it.type.equals("expense", ignoreCase = true) }
        .groupBy { it.category }
        .mapValues { entry ->
            entry.value.sumOf { it.amount }.toFloat()
        }

    var category by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf(
        stringResource(R.string.student),
        stringResource(R.string.working),
        stringResource(R.string.business),
        stringResource(R.string.housewife)
    )

    var ageInput by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    var wantsSaving by remember { mutableStateOf(false) }
    var savingOption by remember { mutableStateOf("PERCENT") }
    var savingInput by remember { mutableStateOf("") }
    var calculatedSaving by remember { mutableStateOf(0.0) }

    val fillRequiredText = stringResource(R.string.fill_required)
    val enterSavingText = stringResource(R.string.enter_saving)
    val savedSuccessText = stringResource(R.string.saved_success)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pastelBg)
            .padding(16.dp)
    ) {

        // 💎 PREDICTION CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = glassColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = stringResource(R.string.setup_budget),
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Predicted Spending: ₹${viewModel.prediction.roundToInt()}")
                Text(viewModel.insight)

                Spacer(modifier = Modifier.height(6.dp))

                viewModel.alerts.forEach {
                    Text(it)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 💎 INPUT SECTION
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = glassColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_category)) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    category = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ageInput,
                    onValueChange = { ageInput = it },
                    label = { Text(stringResource(R.string.age)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = income,
                    onValueChange = { income = it },
                    label = { Text(stringResource(R.string.monthly_income)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text(stringResource(R.string.monthly_budget)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.want_save))
                    Switch(
                        checked = wantsSaving,
                        onCheckedChange = { wantsSaving = it }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (wantsSaving) {

                    Text(stringResource(R.string.saving_type))

                    Row {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(
                                selected = savingOption == "PERCENT",
                                onClick = { savingOption = "PERCENT" }
                            )
                            Text(stringResource(R.string.percentage))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(
                                selected = savingOption == "AMOUNT",
                                onClick = { savingOption = "AMOUNT" }
                            )
                            Text(stringResource(R.string.amount))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = savingInput,
                        onValueChange = { savingInput = it },
                        label = {
                            Text(
                                if (savingOption == "PERCENT")
                                    stringResource(R.string.saving_percent)
                                else
                                    stringResource(R.string.saving_amount)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val incomeDouble = income.toDoubleOrNull() ?: 0.0

                    calculatedSaving = if (savingOption == "PERCENT") {
                        val percent = savingInput.toDoubleOrNull() ?: 0.0
                        (incomeDouble * percent) / 100
                    } else {
                        savingInput.toDoubleOrNull() ?: 0.0
                    }

                    Text("${stringResource(R.string.saving_amount)}: ₹${calculatedSaving.toInt()}")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {

                        if (category.isBlank() ||
                            ageInput.isBlank() ||
                            income.isBlank() ||
                            budget.isBlank()
                        ) {
                            Toast.makeText(context, fillRequiredText, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (wantsSaving && savingInput.isBlank()) {
                            Toast.makeText(context, enterSavingText, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val ageInt = ageInput.toIntOrNull() ?: 0
                        val incomeDoubleFinal = income.toDoubleOrNull() ?: 0.0

                        viewModel.saveDashboardData(ageInt, incomeDoubleFinal)

                        Toast.makeText(context, savedSuccessText, Toast.LENGTH_SHORT).show()

                        navController.navigate("expenseCategory/$ageInt")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.saved_success))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 💎 EXPENSE CHART
        if (expenseMap.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = glassColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = stringResource(R.string.expense_analysis),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PieChartView(data = expenseMap)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 💎 TRANSACTIONS
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = glassColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                Text(
                    text = stringResource(R.string.transactions),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (transactions.isEmpty()) {
                    Text(stringResource(R.string.no_transactions))
                } else {
                    LazyColumn {
                        items(transactions) { transaction ->

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {

                                    Text("₹${transaction.amount}")
                                    Text(transaction.category)
                                    Text(transaction.type)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}