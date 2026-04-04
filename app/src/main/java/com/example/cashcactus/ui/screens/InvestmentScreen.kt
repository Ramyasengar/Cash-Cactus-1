package com.example.cashcactus.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashcactus.viewmodel.InvestmentViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun InvestmentScreen(viewModel: InvestmentViewModel = viewModel()) {

    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

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
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(investment.link))
                        context.startActivity(intent)
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