package com.example.cashcactus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun CashCactusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Green80,
            background = androidx.compose.ui.graphics.Color.Black,
            surface = androidx.compose.ui.graphics.Color.Black,
            onPrimary = androidx.compose.ui.graphics.Color.Black,
            onBackground = androidx.compose.ui.graphics.Color.White,
            onSurface = androidx.compose.ui.graphics.Color.White
        )
    } else {
        lightColorScheme(
            primary = Green40,
            background = androidx.compose.ui.graphics.Color.White,
            surface = androidx.compose.ui.graphics.Color.White,
            onPrimary = androidx.compose.ui.graphics.Color.White,
            onBackground = androidx.compose.ui.graphics.Color.Black,
            onSurface = androidx.compose.ui.graphics.Color.Black
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}