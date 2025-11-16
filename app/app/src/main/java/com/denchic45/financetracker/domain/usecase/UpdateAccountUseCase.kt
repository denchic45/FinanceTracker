package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.api.account.model.UpdateAccountRequest
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.repository.AccountRepository
import java.util.UUID

class UpdateAccountUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(
        accountId: UUID,
        request: UpdateAccountRequest
    ): RequestResult<AccountResponse> {
        return accountRepository.update(accountId, request)
    }
}