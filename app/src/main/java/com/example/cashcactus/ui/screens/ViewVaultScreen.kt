package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.utils.CryptoManager
import com.example.cashcactus.viewmodel.VaultViewModel

@Composable
fun ViewVaultScreen(navController: NavHostController, viewModel: VaultViewModel = viewModel()) {
    val crypto = CryptoManager()
    LaunchedEffect(Unit) { viewModel.loadData() }

    BaseScreen(title = stringResource(R.string.vault_items)) { contentPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate("addVault") }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.add_item)) }
                OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.back)) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (viewModel.vaultList.isEmpty()) Text(stringResource(R.string.no_data))

            LazyColumn {
                items(viewModel.vaultList) { item ->
                    val decrypted = crypto.decrypt(item.encryptedContent, item.iv)
                    CashCactusCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Column {
                            Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = decrypted)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.delete(item) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                                Text(stringResource(R.string.delete), color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
