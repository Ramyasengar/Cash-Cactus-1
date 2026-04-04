package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.data.model.VaultData
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.utils.CryptoManager
import com.example.cashcactus.viewmodel.VaultViewModel

@Composable
fun AddVaultScreen(navController: NavHostController, viewModel: VaultViewModel = viewModel()) {
    val crypto = CryptoManager()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    BaseScreen(title = stringResource(R.string.add_secure_data)) { contentPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.vault_title_hint)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text(stringResource(R.string.secret_info)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        if (title.isNotEmpty() && content.isNotEmpty()) {
                            val (encrypted, iv) = crypto.encrypt(content)
                            viewModel.insert(VaultData(title = title, encryptedContent = encrypted, iv = iv))
                            navController.popBackStack()
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.save_securely)) }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.cancel)) }
                }
            }
        }
    }
}
