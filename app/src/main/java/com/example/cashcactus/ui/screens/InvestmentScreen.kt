package com.example.cashcactus.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.viewmodel.InvestmentViewModel

@Composable
fun InvestmentScreen(viewModel: InvestmentViewModel = viewModel()) {
    val context = LocalContext.current

    val lowRisk = stringResource(R.string.risk_low)

    var selectedRisk by remember { mutableStateOf<String?>(null) }
    var selectedDuration by remember { mutableStateOf<String?>(null) }
    var selectedIncome by remember { mutableStateOf<String?>(null) }
    var recommendation by remember { mutableStateOf<String?>(null) }
    val isQuestionnaireComplete = selectedRisk != null && selectedDuration != null && selectedIncome != null

    BaseScreen(title = stringResource(R.string.investments_title)) { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.investments) { investment ->
                CashCactusCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val rawLink = investment.link.trim()
                            val normalizedLink = when {
                                rawLink.isBlank() -> null
                                rawLink.startsWith("http://", ignoreCase = true) ||
                                    rawLink.startsWith("https://", ignoreCase = true) -> rawLink

                                else -> "https://$rawLink"
                            }

                            if (normalizedLink == null) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.invalid_investment_url),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@clickable
                            }

                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalizedLink))
                                val canResolve = intent.resolveActivity(context.packageManager) != null

                                if (canResolve) {
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.no_app_to_open_url),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (_: Exception) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.unable_to_open_link),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(investment.name, style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.investment_type, investment.type))
                        Text(stringResource(R.string.investment_risk, investment.risk))
                        Text(stringResource(R.string.investment_returns, investment.returns))
                        Text(
                            stringResource(R.string.tap_to_invest),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.find_best_investment),
                            style = MaterialTheme.typography.titleLarge
                        )

                        QuestionGroup(
                            title = stringResource(R.string.question_risk_level),
                            options = listOf(
                                stringResource(R.string.risk_low),
                                stringResource(R.string.risk_medium),
                                stringResource(R.string.risk_high)
                            ),
                            selected = selectedRisk,
                            onSelect = { selectedRisk = it }
                        )

                        QuestionGroup(
                            title = stringResource(R.string.question_investment_duration),
                            options = listOf(
                                stringResource(R.string.duration_short),
                                stringResource(R.string.duration_long)
                            ),
                            selected = selectedDuration,
                            onSelect = { selectedDuration = it }
                        )

                        QuestionGroup(
                            title = stringResource(R.string.question_income_range),
                            options = listOf(
                                stringResource(R.string.income_below_25k),
                                stringResource(R.string.income_25k_50k),
                                stringResource(R.string.income_above_50k)
                            ),
                            selected = selectedIncome,
                            onSelect = { selectedIncome = it }
                        )

                        Button(
                            onClick = {
                                recommendation = when (selectedRisk ?: lowRisk) {
                                    lowRisk -> context.getString(R.string.digital_gold)
                                    context.getString(R.string.risk_medium) -> context.getString(R.string.mutual_fund)
                                    else -> context.getString(R.string.stock_market)
                                }
                            },
                            enabled = isQuestionnaireComplete,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.get_recommendation))
                        }
                    }
                }
            }

            recommendation?.let { result ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.recommended_result, result),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionGroup(
    title: String,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(option) }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected == option, onClick = { onSelect(option) })
                    Text(option, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
