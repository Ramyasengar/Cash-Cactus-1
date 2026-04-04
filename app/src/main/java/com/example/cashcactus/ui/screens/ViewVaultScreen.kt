package com.example.cashcactus.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cashcactus.utils.CryptoManager
import com.example.cashcactus.R
import com.example.cashcactus.viewmodel.VaultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewVaultScreen(
    navController: NavHostController,
    viewModel: VaultViewModel = viewModel()
) {
    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    val crypto = CryptoManager()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.vault_items)) }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("addVault") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.add_item))
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.back))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.vaultList.isEmpty()) {
                Text(stringResource(R.string.no_data), color = Color.LightGray)
            }

            LazyColumn {
                items(viewModel.vaultList) { item ->
                    val decrypted = crypto.decrypt(item.encryptedContent, item.iv)

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = decrypted,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.delete(item) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.delete), color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
