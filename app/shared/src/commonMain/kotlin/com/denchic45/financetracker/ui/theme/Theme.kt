package com.denchic45.financetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.denchic45.financetracker.domain.JVMCurrencyHandler
import com.denchic45.financetracker.domain.model.Currency
import com.denchic45.financetracker.ui.LocalCurrencyHandler
import com.denchic45.financetracker.ui.LocalDefaultCurrency

internal val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

internal val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
expect fun FinanceTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
)

@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    FinanceTrackerTheme {
        CompositionLocalProvider(
            LocalCurrencyHandler provides JVMCurrencyHandler(),
            LocalDefaultCurrency provides Currency.RUB
        ) {
            content()
        }
    }
}