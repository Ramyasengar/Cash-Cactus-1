package com.example.cashcactus.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.viewmodel.InvestmentViewModel

@Composable
fun InvestmentScreen(viewModel: InvestmentViewModel = viewModel()) {
    val context = LocalContext.current

    CashCactusScreenScaffold(title = "Investments") { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Investments", style = MaterialTheme.typography.headlineMedium)
            }

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
                                Toast.makeText(context, "Invalid investment URL", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }

                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalizedLink))
                                val canResolve = intent.resolveActivity(context.packageManager) != null

                                if (canResolve) {
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "No app available to open this URL", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
                            }
                        }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(investment.name, style = MaterialTheme.typography.titleMedium)
                        Text("Type: ${investment.type}")
                        Text("Risk: ${investment.risk}")
                        Text("Returns: ${investment.returns}")
                        Text("Tap to Invest", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
