package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.repository.AccountRepository
import java.util.UUID

class RemoveAccountUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(accountId: UUID): EmptyRequestResult {
        return accountRepository.remove(accountId)
    }
}