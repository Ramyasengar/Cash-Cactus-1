package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import com.example.cashcactus.ui.components.BaseScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R

@Composable
fun AboutScreen(navController: NavHostController) {

    BaseScreen(
        title = stringResource(R.string.about_title),
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text(
                text = stringResource(R.string.about_tagline),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.about_desc),
                style = MaterialTheme.typography.bodyLarge
            )

            HorizontalDivider()

            SectionHeading(stringResource(R.string.section_tracking))
            Text(stringResource(R.string.tracking_desc))

            SectionHeading(stringResource(R.string.section_alerts))
            Text(stringResource(R.string.alerts_desc))

            SectionHeading(stringResource(R.string.section_investment))
            Text(stringResource(R.string.investment_desc))

            SectionHeading(stringResource(R.string.section_security))
            Text(stringResource(R.string.security_desc))

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.about_footer),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}