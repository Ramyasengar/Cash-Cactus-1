package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import com.example.cashcactus.utils.BiometricHelper
import com.example.cashcactus.utils.PinManager
import com.example.cashcactus.R
import com.example.cashcactus.utils.VaultSessionManager
import com.example.cashcactus.utils.findActivity

@Composable
fun VaultEntryScreen(navController: NavHostController) {

    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)
    val context = LocalContext.current
    var handled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (handled) return@LaunchedEffect
        handled = true

        val activity = context.findActivity()

        if (VaultSessionManager.isUnlocked(context)) {
            navController.navigate("vaultHome") {
                popUpTo("vaultEntry") { inclusive = true }
            }
            return@LaunchedEffect
        }

        if (activity == null) {
            navController.navigate(if (PinManager.isPinSet(context)) "vaultPin/unlock" else "vaultPin/setup") {
                popUpTo("vaultEntry") { inclusive = true }
            }
            return@LaunchedEffect
        }

        if (BiometricHelper.isBiometricAvailable(context)) {
            BiometricHelper.showBiometricPrompt(
                activity = activity,
                onSuccess = {
                    VaultSessionManager.unlock(context)
                    navController.navigate("vaultHome") {
                        popUpTo("vaultEntry") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = {
                    navController.navigate(if (PinManager.isPinSet(context)) "vaultPin/unlock" else "vaultPin/setup") {
                        popUpTo("vaultEntry") { inclusive = true }
                    }
                }
            )
        } else {
            navController.navigate(if (PinManager.isPinSet(context)) "vaultPin/unlock" else "vaultPin/setup") {
                popUpTo("vaultEntry") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.unlocking_vault))
    }
}
