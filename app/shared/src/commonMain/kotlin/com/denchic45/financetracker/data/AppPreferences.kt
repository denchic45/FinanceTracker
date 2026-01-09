package com.denchic45.financetracker.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.denchic45.financetracker.domain.model.Currency
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull


class AppPreferences(private val dataStore: DataStore<Preferences>) {
    private val accessTokenKey = stringPreferencesKey("token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val defaultCurrencyKey = stringPreferencesKey("default_currency")

    val accessToken = dataStore.data.map { it[accessTokenKey] }
    val refreshToken = dataStore.data.map { it[refreshTokenKey] }
    val defaultCurrency = dataStore.data.mapNotNull {
        val currencyString = it[defaultCurrencyKey]
            ?: throw IllegalStateException("Default currency required")
        Currency.fromSymbol(currencyString.single())
    }

    suspend fun setAccessToken(token: String) {
        dataStore.edit { it[accessTokenKey] = token }
    }

    suspend fun setRefreshToken(token: String) {
        dataStore.edit { it[refreshTokenKey] = token }
    }

    suspend fun setDefaultCurrency(currency: Currency) {
        dataStore.edit { it[defaultCurrencyKey] = currency.symbol.toString() }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}