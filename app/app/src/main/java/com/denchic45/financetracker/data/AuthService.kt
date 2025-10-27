package com.denchic45.financetracker.data

import arrow.core.None
import arrow.core.Option
import arrow.core.some
import com.denchic45.financetracker.auth.AuthApi
import com.denchic45.financetracker.auth.model.SignInRequest
import com.denchic45.financetracker.auth.model.SignUpRequest
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.response.Failure
import com.denchic45.financetracker.response.toOption
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

class AuthService(
    private val database: AppDatabase,
    private val client: HttpClient,
    private val authApi: AuthApi,
    private val appPreferences: AppPreferences
) {

    val observeIsAuthenticated: Flow<Boolean>
        get() = appPreferences.token
            .mapNotNull { !it.isNullOrBlank() }
            .onEach { isAuth ->
                if (!isAuth)
                    clearAllData()
            }

    private suspend fun clearAllData() {
        database.clearAllTables()
        appPreferences.clearAll()

        // clear tokens
        val plugin = client.plugin(Auth)
        plugin.close()
//        val provider = plugin.providers.filterIsInstance<BearerAuthProvider>().first()
//        provider.clearToken()
    }

    suspend fun signUp(request: SignUpRequest): Option<Failure> {
        return authApi.signUp(request)
            .fold(
                ifLeft = { it.some() },
                ifRight = {
                    appPreferences.setToken(it)
                    None
                })
    }

    suspend fun signIn(request: SignInRequest): Option<Failure> {
        return authApi.signIn(request).onRight { appPreferences.setToken(it) }
            .toOption()
    }
}