package com.denchic45.financetracker.domain.usecase

import arrow.core.Ior
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.domain.model.AccountItem
import kotlinx.coroutines.flow.Flow

class ObserveAccountsUseCase(private val accountRepository: AccountRepository) {
    operator fun invoke(): Flow<Ior<Failure, List<AccountItem>>> {
        return accountRepository.observe()
    }
}