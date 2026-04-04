package com.example.cashcactus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import com.example.cashcactus.utils.CryptoManager
import com.example.cashcactus.R
import com.example.cashcactus.data.model.VaultData
import com.example.cashcactus.viewmodel.VaultViewModel

@Composable
fun AddVaultScreen(
    navController: NavHostController,
    viewModel: VaultViewModel = viewModel()
) {

    val crypto = CryptoManager()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B)) // ✅ slightly lighter dark
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🔐 TITLE
        Text(
            text = stringResource(R.string.add_secure_data),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 📌 TITLE FIELD
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.vault_title_hint)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,

                focusedBorderColor = Color(0xFF22C55E),
                unfocusedBorderColor = Color.Gray,

                focusedLabelColor = Color(0xFF22C55E),
                unfocusedLabelColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔑 CONTENT FIELD
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text(stringResource(R.string.secret_info)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,

                focusedBorderColor = Color(0xFF22C55E),
                unfocusedBorderColor = Color.Gray,

                focusedLabelColor = Color(0xFF22C55E),
                unfocusedLabelColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 💾 SAVE BUTTON
        Button(
            onClick = {

                if (title.isNotEmpty() && content.isNotEmpty()) {

                    val (encrypted, iv) = crypto.encrypt(content)

                    viewModel.insert(
                        VaultData(
                            title = title,
                            encryptedContent = encrypted,
                            iv = iv
                        )
                    )

                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.save_securely))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ❌ CANCEL BUTTON
        OutlinedButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}