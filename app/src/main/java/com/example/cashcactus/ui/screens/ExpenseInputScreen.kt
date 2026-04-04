package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.cashcactus.viewmodel.MainViewModel

@Composable
fun ExpenseInputScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {

    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Add Expense", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (Food, Rent, etc)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (amount.isBlank() || category.isBlank()) {
                    Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                viewModel.addTransaction(
                    amount = amount.toDouble(),
                    type = "expense",
                    category = category,
                    message = "Manual Entry"
                )

                Toast.makeText(context, "Expense Added", Toast.LENGTH_SHORT).show()

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Expense")
        }
    }
}