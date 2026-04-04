package com.example.cashcactus.ui.screens

import android.content.Context
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.ui.components.CashCactusScreenScaffold
import com.example.cashcactus.utils.UserSessionManager
import com.google.firebase.auth.FirebaseAuth

private const val SETTINGS_PREF = "app_settings"
private const val KEY_SELECTED_LANGUAGE = "selected_language"

@Composable
fun SettingsScreen(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    val languagePref = remember {
        context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE)
    }
    var selectedLanguage by rememberSaveable {
        mutableStateOf(languagePref.getString(KEY_SELECTED_LANGUAGE, "English") ?: "English")
    }

    CashCactusScreenScaffold(title = "Settings") { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Theme", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (isDarkTheme) "Dark Mode" else "Light Mode")
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
                        Text("Language", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))

                        listOf("English", "Hindi").forEach { language ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedLanguage == language,
                                    onClick = {
                                        selectedLanguage = language
                                        languagePref.edit().putString(KEY_SELECTED_LANGUAGE, language).apply()
                                    }
                                )
                                Text(language)
                            }
                        }
                    }
                }
            }

            item {
                CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Account", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete Account")
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account session on this device?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        UserSessionManager.clearSession(context)
                        FirebaseAuth.getInstance().signOut()
                        showDeleteDialog = false
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
