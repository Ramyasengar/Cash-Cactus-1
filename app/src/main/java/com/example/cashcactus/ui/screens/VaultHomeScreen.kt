package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.utils.VaultSessionManager

@Composable
fun VaultHomeScreen(navController: NavController) {
    val context = LocalContext.current
    BaseScreen(title = stringResource(R.string.vault_title)) { contentPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(contentPadding), verticalArrangement = Arrangement.Center) {
            CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = stringResource(R.string.vault_title), style = MaterialTheme.typography.headlineSmall)
                    Text(text = stringResource(R.string.vault_desc), style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { navController.navigate("addVault") }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.add_item)) }
                    Button(onClick = { navController.navigate("viewVault") }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.view_items)) }
                    Button(onClick = { navController.navigate("emergency") }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.emergency_sharing)) }
                    OutlinedButton(onClick = {
                        VaultSessionManager.lock(context)
                        navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true }
                    }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.lock_vault)) }
                }
            }
        }
    }
}
