package com.example.cashcactus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.CashCactusCard

@Composable
fun HelpScreen(navController: NavHostController) {
    BaseScreen(
        title = stringResource(R.string.help_support),
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExpandableSection(stringResource(R.string.getting_started), stringResource(R.string.getting_started_desc))
            ExpandableSection(stringResource(R.string.managing_expenses), stringResource(R.string.managing_expenses_desc))
            ExpandableSection(stringResource(R.string.investment_guidance), stringResource(R.string.investment_guidance_desc))
            ExpandableSection(stringResource(R.string.account_security), stringResource(R.string.account_security_desc))
            ExpandableSection(stringResource(R.string.troubleshooting), stringResource(R.string.troubleshooting_desc))
            ExpandableSection(stringResource(R.string.contact_support), stringResource(R.string.contact_support_desc))
        }
    }
}

@Composable
fun ExpandableSection(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }
    CashCactusCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.clickable { expanded = !expanded }.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(content, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
