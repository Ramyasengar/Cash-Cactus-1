package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.viewmodel.MainViewModel

@Composable
fun ExpenseCategoryScreen(
    navController: NavHostController,
    age: Int,
    viewModel: MainViewModel
) {
    var food by remember { mutableStateOf("") }
    var rent by remember { mutableStateOf("") }
    var medical by remember { mutableStateOf("") }
    var emi by remember { mutableStateOf("") }
    var additional by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fun fmt(v: Double) = when {
            v <= 0 -> ""
            v % 1.0 == 0.0 -> v.toLong().toString()
            else -> v.toString()
        }
        food = fmt(viewModel.food)
        rent = fmt(viewModel.rent)
        medical = fmt(viewModel.medical)
        emi = fmt(viewModel.emi)
        education = fmt(viewModel.education)
        additional = fmt(viewModel.additional)
    }

    CashCactusScreenScaffold(title = stringResource(R.string.monthly_expenses)) { contentPadding ->
        CashCactusCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = stringResource(R.string.monthly_expenses), style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = food,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) food = it },
                    label = { Text(stringResource(R.string.food)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rent,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) rent = it },
                    label = { Text(stringResource(R.string.rent)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = medical,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) medical = it },
                    label = { Text(stringResource(R.string.medical)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (age > 18) {
                    OutlinedTextField(
                        value = emi,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) emi = it },
                        label = { Text(stringResource(R.string.emi)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = additional,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) additional = it },
                    label = { Text(stringResource(R.string.additional)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = education,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) education = it },
                    label = { Text(stringResource(R.string.education)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                val foodAmount = food.toDoubleOrNull() ?: 0.0
                val rentAmount = rent.toDoubleOrNull() ?: 0.0
                val medicalAmount = medical.toDoubleOrNull() ?: 0.0
                val emiAmount = emi.toDoubleOrNull() ?: 0.0
                val educationAmount = education.toDoubleOrNull() ?: 0.0
                val additionalAmount = additional.toDoubleOrNull() ?: 0.0

                Button(
                    onClick = {
                        viewModel.food = foodAmount
                        viewModel.rent = rentAmount
                        viewModel.medical = medicalAmount
                        viewModel.emi = emiAmount
                        viewModel.additional = additionalAmount
                        viewModel.education = educationAmount

                        viewModel.saveMonthlyExpenses(
                            food = foodAmount,
                            rent = rentAmount,
                            medical = medicalAmount,
                            emi = emiAmount,
                            education = educationAmount,
                            additional = additionalAmount
                        )
                        navController.navigate("expenseGraph")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
