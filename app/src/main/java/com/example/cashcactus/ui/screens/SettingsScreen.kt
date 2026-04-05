package com.example.cashcactus.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.utils.LanguageManager
import com.example.cashcactus.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteInProgress by remember { mutableStateOf(false) }
    var selectedLanguage by remember {
        mutableStateOf(LanguageManager.getSavedLanguage(context))
    }

    CashCactusScreenScaffold(title = stringResource(R.string.settings)) { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (isDarkTheme) stringResource(R.string.dark_mode) else stringResource(R.string.light_mode))
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { enabled -> onThemeChange(enabled) }
                            )
                        }
                    }
                }
            }

            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(stringResource(R.string.language), style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            LanguageManager.ENGLISH to stringResource(R.string.english),
                            LanguageManager.HINDI to stringResource(R.string.hindi)
                        ).forEach { (languageCode, languageLabel) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedLanguage == languageCode,
                                    onClick = {
                                        if (selectedLanguage != languageCode) {
                                            selectedLanguage = languageCode
                                            LanguageManager.setLanguage(context, languageCode)
                                            onLanguageChange(languageCode)
                                        }
                                    }
                                )
                                Text(languageLabel)
                            }
                        }
                    }
                }
            }

            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(stringResource(R.string.account), style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.delete_account))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_account)) },
            text = { Text(stringResource(R.string.delete_account_confirmation)) },
            confirmButton = {
                TextButton(
                    enabled = !deleteInProgress,
                    onClick = {
                        if (deleteInProgress) return@TextButton
                        deleteInProgress = true
                        viewModel.deleteAccount { success, errorMessage ->
                            deleteInProgress = false
                            if (success) {
                                showDeleteDialog = false
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    errorMessage ?: context.getString(R.string.delete_account_failed),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
