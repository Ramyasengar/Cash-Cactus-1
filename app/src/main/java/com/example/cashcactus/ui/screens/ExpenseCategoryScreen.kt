package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.viewmodel.MainViewModel

@Composable
fun ExpenseCategoryScreen(
    navController: NavHostController,
    age: Int,
    viewModel: MainViewModel
) {
    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    var food by remember { mutableStateOf("") }
    var rent by remember { mutableStateOf("") }
    var medical by remember { mutableStateOf("") }
    var emi by remember { mutableStateOf("") }
    var additional by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = stringResource(R.string.monthly_expenses),
            style = MaterialTheme.typography.headlineSmall
        )

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

                viewModel.food = food.toDoubleOrNull() ?: 0.0
                viewModel.rent = rent.toDoubleOrNull() ?: 0.0
                viewModel.medical = medical.toDoubleOrNull() ?: 0.0
                viewModel.emi = emi.toDoubleOrNull() ?: 0.0
                viewModel.additional = additional.toDoubleOrNull() ?: 0.0
                viewModel.education = education.toDoubleOrNull() ?: 0.0

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