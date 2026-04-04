package com.example.cashcactus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavHostController) {
    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.help_support)) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ExpandableSection(
                stringResource(R.string.getting_started),
                stringResource(R.string.getting_started_desc)
            )

            ExpandableSection(
                stringResource(R.string.managing_expenses),
                stringResource(R.string.managing_expenses_desc)
            )

            ExpandableSection(
                stringResource(R.string.investment_guidance),
                stringResource(R.string.investment_guidance_desc)
            )

            ExpandableSection(
                stringResource(R.string.account_security),
                stringResource(R.string.account_security_desc)
            )

            ExpandableSection(
                stringResource(R.string.troubleshooting),
                stringResource(R.string.troubleshooting_desc)
            )

            ExpandableSection(
                stringResource(R.string.contact_support),
                stringResource(R.string.contact_support_desc)
            )
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    content: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)

                Icon(
                    imageVector =
                        if (expanded)
                            Icons.Default.ExpandLess
                        else
                            Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(content, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}