package com.denchic45.financetracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppPreferences(private val dataStore: DataStore<Preferences>) {
    private val accessTokenKey = stringPreferencesKey("token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    val accessToken = dataStore.data.map { it[accessTokenKey] }
    val refreshToken = dataStore.data.map { it[refreshTokenKey] }

    suspend fun setAccessToken(token: String) {
        dataStore.edit { it[accessTokenKey] = token }
    }

    suspend fun setRefreshToken(token: String) {
        dataStore.edit { it[accessTokenKey] = token }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}