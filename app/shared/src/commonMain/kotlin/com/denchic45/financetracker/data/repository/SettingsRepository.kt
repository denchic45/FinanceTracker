package com.denchic45.financetracker.data.repository

import com.denchic45.financetracker.data.AppPreferences
import com.denchic45.financetracker.domain.model.Currency
import kotlinx.coroutines.flow.Flow

class SettingsRepository(val preferences: AppPreferences) {

    val selectedCurrency: Flow<Currency> = preferences.defaultCurrency

    suspend fun updateCurrency(currency: Currency) {
        preferences.setDefaultCurrency(currency)
    }
}