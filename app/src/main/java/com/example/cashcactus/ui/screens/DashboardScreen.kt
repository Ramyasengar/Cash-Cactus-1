package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.ui.components.PieChartView
import com.example.cashcactus.viewmodel.MainViewModel
import com.example.cashcactus.viewmodel.TransactionViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel()
    val transactions by transactionViewModel.allTransactions.collectAsState(initial = emptyList())

    val expenseMap = transactions
        .filter { it.type.equals("expense", ignoreCase = true) }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount }.toFloat() }

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

    CashCactusScreenScaffold(title = stringResource(R.string.dashboard)) { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = stringResource(R.string.setup_budget),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Predicted Spending: ₹${viewModel.prediction.roundToInt()}")
                        Text(viewModel.insight)
                        Spacer(modifier = Modifier.height(6.dp))
                        viewModel.alerts.forEach { Text(it) }
                    }
                }
            }

            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.select_category)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
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
                        OutlinedTextField(value = ageInput, onValueChange = { ageInput = it }, label = { Text(stringResource(R.string.age)) }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = income, onValueChange = { income = it }, label = { Text(stringResource(R.string.monthly_income)) }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = budget, onValueChange = { budget = it }, label = { Text(stringResource(R.string.monthly_budget)) }, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(15.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.want_save))
                            Switch(checked = wantsSaving, onCheckedChange = { wantsSaving = it })
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        if (wantsSaving) {
                            Text(stringResource(R.string.saving_type))
                            Row {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = savingOption == "PERCENT", onClick = { savingOption = "PERCENT" })
                                    Text(stringResource(R.string.percentage))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = savingOption == "AMOUNT", onClick = { savingOption = "AMOUNT" })
                                    Text(stringResource(R.string.amount))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = savingInput,
                                onValueChange = { savingInput = it },
                                label = {
                                    Text(
                                        if (savingOption == "PERCENT") stringResource(R.string.saving_percent)
                                        else stringResource(R.string.saving_amount)
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
                                if (category.isBlank() || ageInput.isBlank() || income.isBlank() || budget.isBlank()) {
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
            }

            if (expenseMap.isNotEmpty()) {
                item {
                    CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(text = stringResource(R.string.expense_analysis), style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(10.dp))
                            PieChartView(data = expenseMap)
                        }
                    }
                }
            }

            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(text = stringResource(R.string.transactions), style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        if (transactions.isEmpty()) {
                            Text(stringResource(R.string.no_transactions))
                        } else {
                            transactions.forEach { transaction ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
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
}
