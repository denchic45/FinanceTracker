package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.response.EmptyResponseResult
import java.util.UUID

class RemoveAccountUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(accountId: UUID): EmptyResponseResult {
        return accountRepository.remove(accountId)
    }
}