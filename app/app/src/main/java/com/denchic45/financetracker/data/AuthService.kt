package com.denchic45.financetracker.data

import com.denchic45.financetracker.auth.AuthApi
import com.denchic45.financetracker.auth.model.SignUpRequest
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.domain.onSuccess

class AuthService(
    private val database: AppDatabase,
    private val authApi: AuthApi,
    private val appPreferences: AppPreferences
) {
    suspend fun signUp(request: SignUpRequest) {
        fetchResource { authApi.signUp(request) }
            .onSuccess {
                appPreferences.token
            }
    }
}