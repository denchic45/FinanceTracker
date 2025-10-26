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

class AuthService(
    private val database: AppDatabase,
    private val authApi: AuthApi,
    private val appPreferences: AppPreferences
) {
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