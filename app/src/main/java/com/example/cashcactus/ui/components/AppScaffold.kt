package com.example.cashcactus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DashboardBackground = Color(0xFF796A6A)
private val DashboardGlassCard = Color.White.copy(alpha = 0.15f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashCactusScreenScaffold(
    title: String,
    modifier: Modifier = Modifier,
    topBarActions: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = DashboardBackground,
        topBar = {
            TopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DashboardBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = { topBarActions() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DashboardBackground)
                .padding(paddingValues)
        ) {
            content(PaddingValues(horizontal = 16.dp, vertical = 12.dp))
        }
    }
}

@Composable
fun CashCactusCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardGlassCard),
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
