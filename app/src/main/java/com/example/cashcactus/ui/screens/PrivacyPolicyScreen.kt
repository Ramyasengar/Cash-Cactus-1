package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.utils.PrivacyHelper
import com.example.cashcactus.utils.UserSessionManager

@Composable
fun PrivacyPolicyScreen(
    navController: NavHostController,
    isMandatory: Boolean = false
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isChecked by remember { mutableStateOf(false) }
    var hasReachedBottom by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value) {
        if (scrollState.value == scrollState.maxValue) {
            hasReachedBottom = true
        }
    }

    BaseScreen(
        title = stringResource(R.string.privacy_policy),
        navigationIcon = if (isMandatory) null else {
            {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PrivacyCard(stringResource(R.string.privacy_welcome))
                PrivacyCard(stringResource(R.string.privacy_desc))
                PrivacyCard(stringResource(R.string.info_collect))
                PrivacyCard(stringResource(R.string.data_use))
                PrivacyCard(stringResource(R.string.data_security))
                PrivacyCard(stringResource(R.string.permissions))
                PrivacyCard(stringResource(R.string.user_control))
                PrivacyCard(stringResource(R.string.updates))
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (isMandatory) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
                    Text(stringResource(R.string.agree_policy))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        PrivacyHelper.setAccepted(context)
                        val next = if (UserSessionManager.isLoggedIn(context)) "home" else "login"
                        navController.navigate(next) {
                            popUpTo("privacy_mandatory") { inclusive = true }
                        }
                    },
                    enabled = isChecked && hasReachedBottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.accept))
                }
            }
        }
    }
}

@Composable
fun PrivacyCard(text: String) {
    CashCactusCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
