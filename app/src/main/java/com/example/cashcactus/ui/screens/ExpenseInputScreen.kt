package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.viewmodel.MainViewModel

@Composable
fun ExpenseInputScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    CashCactusScreenScaffold(title = "Add Expense") { contentPadding ->
        CashCactusCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            Column {
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
    }
}
