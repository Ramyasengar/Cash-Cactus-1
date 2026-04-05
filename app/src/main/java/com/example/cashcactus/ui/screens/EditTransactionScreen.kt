package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.viewmodel.TransactionViewModel

@Composable
fun EditTransactionScreen(
    navController: NavHostController,
    transactionId: Int
) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel()
    val transactions by transactionViewModel.allTransactions.collectAsState(initial = emptyList())
    val tx = remember(transactionId, transactions) { transactions.find { it.id == transactionId } }

    var amountText by remember { mutableStateOf("") }
    var categoryText by remember { mutableStateOf("") }

    LaunchedEffect(tx) {
        val t = tx ?: return@LaunchedEffect
        amountText = if (t.amount % 1.0 == 0.0) t.amount.toInt().toString() else t.amount.toString()
        categoryText = t.category
    }

    CashCactusScreenScaffold(title = stringResource(R.string.edit_expense)) { contentPadding ->
        if (tx == null) {
            Text(
                text = stringResource(R.string.transaction_not_found),
                modifier = Modifier.padding(contentPadding),
                style = MaterialTheme.typography.bodyLarge
            )
            return@CashCactusScreenScaffold
        }

        CashCactusCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoryText,
                    onValueChange = { categoryText = it },
                    label = { Text(stringResource(R.string.category_expense_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || categoryText.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.fill_required), Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        transactionViewModel.updateTransaction(
                            tx.copy(amount = amount, category = categoryText.trim())
                        )
                        Toast.makeText(context, context.getString(R.string.saved_success), Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
