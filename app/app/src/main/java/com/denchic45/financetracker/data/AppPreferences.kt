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
    private val tokenKey = stringPreferencesKey("token")

    val token = dataStore.data.map { it[tokenKey] }

    suspend fun setToken(token: String) {
        dataStore.edit { it[tokenKey] = token }
    }
}