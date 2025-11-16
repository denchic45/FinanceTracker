package com.denchic45.financetracker.domain.usecase

import arrow.core.Option
import com.denchic45.financetracker.api.auth.model.SignUpRequest
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.service.AuthService

class SignUpUseCase(private val authService: AuthService) {
    suspend operator fun invoke(request: SignUpRequest): Option<Failure> {
        return authService.signUp(request)
    }
}