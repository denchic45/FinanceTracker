package com.denchic45.financetracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.denchic45.financetracker.domain.CurrencyHandler
import com.denchic45.financetracker.domain.model.Currency

val LocalCurrencyHandler = staticCompositionLocalOf<CurrencyHandler> { error("No Handler") }
val LocalDefaultCurrency = staticCompositionLocalOf<Currency> { error("No default currency") }

@get:Composable
val Long.displayedAmount: String
    get() {
        val handler = LocalCurrencyHandler.current
        val currency = LocalDefaultCurrency.current
        return handler.formatForDisplay(this, currency)
    }