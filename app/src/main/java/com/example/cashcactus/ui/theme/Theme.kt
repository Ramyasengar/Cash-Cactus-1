package com.example.cashcactus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = CactusPrimary,
    secondary = CactusPrimaryDark,
    background = CactusBackgroundDark,
    surface = CactusSurfaceDark,
    onPrimary = CactusOnLight,
    onBackground = CactusOnDark,
    onSurface = CactusOnDark
)

private val LightColors = lightColorScheme(
    primary = CactusPrimaryDark,
    secondary = CactusPrimary,
    background = CactusBackground,
    surface = CactusSurface,
    onPrimary = CactusOnDark,
    onBackground = CactusOnDark,
    onSurface = CactusOnDark
)

@Composable
fun CashCactusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
