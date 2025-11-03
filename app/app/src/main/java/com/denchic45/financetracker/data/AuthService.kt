package com.denchic45.financetracker.data

import arrow.core.None
import arrow.core.Option
import arrow.core.some
import com.denchic45.financetracker.auth.AuthApi
import com.denchic45.financetracker.auth.model.AuthResponse
import com.denchic45.financetracker.auth.model.SignInRequest
import com.denchic45.financetracker.auth.model.SignUpRequest
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.repository.safeFetch
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AuthService(
    private val database: AppDatabase,
    private val client: HttpClient,
    private val authApi: AuthApi,
    private val appPreferences: AppPreferences
) {

    val observeIsAuthenticated: Flow<Boolean>
        get() = appPreferences.accessToken
            .mapNotNull { !it.isNullOrBlank() }
            .onEach { isAuth ->
                if (!isAuth)
                    clearAllData()
            }

    private suspend fun clearAllData() {
        coroutineScope { launch(Dispatchers.IO) { database.clearAllTables() } }
        appPreferences.clearAll()

        // clear tokens
        val plugin = client.plugin(Auth)
        plugin.close()
    }

    suspend fun signUp(request: SignUpRequest): Option<Failure> {
        return safeFetch { authApi.signUp(request) }
            .fold(
                ifLeft = { it.some() },
                ifRight = {
                    saveTokens(it)
                    None
                })
    }

    private suspend fun saveTokens(response: AuthResponse) {
        appPreferences.setAccessToken(response.token)
        appPreferences.setRefreshToken(response.refreshToken)
    }

    suspend fun signIn(request: SignInRequest): Option<Failure> {
        return safeFetch { authApi.signIn(request) }.onRight { saveTokens(it) }
            .toEmptyRequestResult()
    }
}