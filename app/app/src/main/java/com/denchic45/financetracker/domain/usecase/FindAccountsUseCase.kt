package com.denchic45.financetracker.domain.usecase

import arrow.core.Ior
import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.response.Failure
import kotlinx.coroutines.flow.Flow

class FindAccountsUseCase(private val accountRepository: AccountRepository) {
    operator fun invoke(): Flow<Ior<Failure, List<AccountResponse>>> {
        return accountRepository.findAll()
    }
}