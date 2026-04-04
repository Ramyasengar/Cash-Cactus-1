package com.example.cashcactus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import com.example.cashcactus.R
import com.example.cashcactus.utils.VaultSessionManager

@Composable
fun VaultHomeScreen(navController: NavController) {

    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF052E16), Color(0xFF14532D)))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.vault_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.vault_desc),
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = { navController.navigate("addVault") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.add_item))
                }

                Button(
                    onClick = { navController.navigate("viewVault") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.view_items))
                }

                Button(
                    onClick = { navController.navigate("emergency") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.emergency_sharing))
                }

                OutlinedButton(
                    onClick = {
                        VaultSessionManager.lock(context)
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.lock_vault))
                }
            }
        }
    }
}
