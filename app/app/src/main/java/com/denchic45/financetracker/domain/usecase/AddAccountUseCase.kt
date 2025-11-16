package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.api.account.model.CreateAccountRequest
import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.repository.AccountRepository

class AddAccountUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(request: CreateAccountRequest): RequestResult<AccountResponse> {
        return accountRepository.add(request)
    }
}