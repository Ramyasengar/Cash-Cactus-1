package com.example.cashcactus.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
    val aiExpenseMap = buildOptimizedExpenseMap(expenseMap)

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

    val savedPeriod = remember { getAnalyticsPeriod(context) }
    var selectedStartDate by remember { mutableStateOf(savedPeriod?.startDateMillis ?: startOfCurrentMonthMillis()) }
    var selectedEndDate by remember { mutableStateOf(savedPeriod?.endDateMillis ?: endOfCurrentMonthMillis()) }

    val fillRequiredText = stringResource(R.string.fill_required)
    val enterSavingText = stringResource(R.string.enter_saving)
    val savedSuccessText = stringResource(R.string.saved_success)
    var selectedGraphPreference by remember {
        mutableStateOf(
            when (getGraphPreference(context)) {
                GRAPH_USER -> GRAPH_USER
                GRAPH_AI -> GRAPH_AI
                else -> null
            }
        )
    }

    CashCactusScreenScaffold(title = stringResource(R.string.dashboard)) { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = stringResource(R.string.monthly_budget),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(modifier = Modifier.height(10.dp))
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

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Time Period", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    showDatePicker(context, selectedStartDate) { selectedStartDate = it }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Start: ${formatDate(selectedStartDate)}")
                            }
                            Button(
                                onClick = {
                                    showDatePicker(context, selectedEndDate) { selectedEndDate = it }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("End: ${formatDate(selectedEndDate)}")
                            }
                        }

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
                                if (selectedStartDate > selectedEndDate) {
                                    Toast.makeText(context, "Start date must be before end date", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
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
                                val budgetLimit = budget.toDoubleOrNull() ?: 0.0

                                saveAnalyticsPeriod(context, selectedStartDate, selectedEndDate)
                                saveBudgetLimit(context, budgetLimit)

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
                            Text(text = stringResource(R.string.user_graph_title), style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(10.dp))
                            PieChartView(data = expenseMap)
                        }
                    }
                }

                item {
                    CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(text = stringResource(R.string.ai_graph_title), style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(10.dp))
                            PieChartView(data = aiExpenseMap)
                        }
                    }
                }

                item {
                    CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.choose_preferred_plan),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedGraphPreference == GRAPH_USER,
                                    onClick = {
                                        selectedGraphPreference = GRAPH_USER
                                        saveGraphPreference(context, GRAPH_USER)
                                    }
                                )
                                Text(text = stringResource(R.string.use_actual_spending))
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedGraphPreference == GRAPH_AI,
                                    onClick = {
                                        selectedGraphPreference = GRAPH_AI
                                        saveGraphPreference(context, GRAPH_AI)
                                    }
                                )
                                Text(text = stringResource(R.string.use_ai_optimized_plan))
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.back_home))
                }
            }
        }
    }
}

private fun showDatePicker(context: android.content.Context, initialMillis: Long, onDateSelected: (Long) -> Unit) {
    val cal = Calendar.getInstance().apply { timeInMillis = initialMillis }
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selected = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onDateSelected(selected.timeInMillis)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

private fun startOfCurrentMonthMillis(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun endOfCurrentMonthMillis(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}
