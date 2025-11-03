package com.denchic45.financetracker.domain.usecase

import arrow.core.Option
import com.denchic45.financetracker.auth.model.SignUpRequest
import com.denchic45.financetracker.data.AuthService
import com.denchic45.financetracker.data.Failure

class SignUpUseCase(private val authService: AuthService) {
    suspend operator fun invoke(request: SignUpRequest): Option<Failure> {
        return authService.signUp(request)
    }
}