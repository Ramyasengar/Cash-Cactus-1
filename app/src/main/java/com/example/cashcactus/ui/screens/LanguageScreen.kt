package com.example.cashcactus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.LanguageManager


@Composable
fun LanguageScreen(navController: NavHostController) {

    val context = LocalContext.current
    val pastelBg = Color(0xFF796A6A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pastelBg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Select Language", color = Color.White)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                LanguageManager.saveLanguage(context, "en")

                navController.navigate("home") {
                    popUpTo("language") { inclusive = true }
                }
            }
        ) {
            Text("English")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                LanguageManager.saveLanguage(context, "hi")

                navController.navigate("home") {
                    popUpTo("language") { inclusive = true }
                }
            }
        ) {
            Text("हिंदी")
        }
    }
}