package com.denchic45.financetracker.domain.usecase

import arrow.core.Option
import com.denchic45.financetracker.auth.model.SignInRequest
import com.denchic45.financetracker.data.AuthService
import com.denchic45.financetracker.data.Failure

class SignInUseCase(private val authService: AuthService) {
    suspend operator fun invoke(request: SignInRequest): Option<Failure> {
        return authService.signIn(request)
    }
}