package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.data.service.AuthService
import kotlinx.coroutines.flow.Flow

class ObserveAuthStateUseCase(
    private val authService: AuthService,
) {

    operator fun invoke(): Flow<Boolean> {
        return authService.observeIsAuthenticated
    }
}