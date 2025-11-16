package com.denchic45.financetracker.domain.usecase

import arrow.core.Option
import com.denchic45.financetracker.api.auth.model.SignInRequest
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.service.AuthService

class SignInUseCase(private val authService: AuthService) {
    suspend operator fun invoke(request: SignInRequest): Option<Failure> {
        return authService.signIn(request)
    }
}