package com.example.cashcactus.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashcactus.viewmodel.InvestmentViewModel

@Composable
fun InvestmentScreen(viewModel: InvestmentViewModel = viewModel()) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Investments", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        viewModel.investments.forEach { investment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
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
                Column(modifier = Modifier.padding(16.dp)) {
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
