package com.example.cashcactus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.cashcactus.utils.PrivacyHelper
import com.example.cashcactus.R
import com.example.cashcactus.utils.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    navController: NavHostController,
    isMandatory: Boolean = false
) {


    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isChecked by remember { mutableStateOf(false) }
    var hasReachedBottom by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value) {
        if (scrollState.value == scrollState.maxValue) {
            hasReachedBottom = true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.privacy_policy)) },
                navigationIcon = {
                    if (!isMandatory) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                )
                .padding(paddingValues)
                .padding(16.dp)
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
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it }
                    )
                    Text(
                        stringResource(R.string.agree_policy),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        PrivacyHelper.setAccepted(context)

                        val next =
                            if (UserSessionManager.isLoggedIn(context)) "home"
                            else "login"

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
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}