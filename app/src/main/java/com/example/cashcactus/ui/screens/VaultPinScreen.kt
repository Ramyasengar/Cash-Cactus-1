package com.example.cashcactus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import com.example.cashcactus.utils.EmergencyAccessManager
import com.example.cashcactus.utils.PinManager
import com.example.cashcactus.R
import com.example.cashcactus.utils.VaultSessionManager

@Composable
fun VaultPinScreen(
    navController: NavController,
    mode: String
) {
    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    val context = LocalContext.current
    val isSetupMode = mode == "setup"

    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))),
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
                    text = if (isSetupMode)
                        stringResource(R.string.create_pin)
                    else
                        stringResource(R.string.unlock_vault),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = if (isSetupMode) {
                        stringResource(R.string.set_pin_desc)
                    } else {
                        stringResource(R.string.unlock_pin_desc)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pin = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.pin_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation()
                )

                if (isSetupMode) {
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all(Char::isDigit)) confirmPin = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.confirm_pin)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

                if (error.isNotBlank()) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        when {
                            pin.length != 4 -> error = context.getString(R.string.pin_length_error)
                            isSetupMode && pin != confirmPin -> error = context.getString(R.string.pin_mismatch)
                            isSetupMode -> {
                                PinManager.savePin(context, pin)
                                VaultSessionManager.unlock(context)
                                navController.navigate("vaultHome") {
                                    popUpTo("vaultEntry") { inclusive = true }
                                }
                            }
                            PinManager.verifyPin(
                                context,
                                pin
                            ) || EmergencyAccessManager.verifyTemporaryPin(context, pin) -> {
                                VaultSessionManager.unlock(context)
                                navController.navigate("vaultHome") {
                                    popUpTo("vaultEntry") { inclusive = true }
                                }
                            }
                            else ->error = context.getString(R.string.incorrect_pin)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isSetupMode)
                            stringResource(R.string.save_pin)
                        else
                            stringResource(R.string.unlock_vault_button)
                    )
                }
            }
        }
    }
}
