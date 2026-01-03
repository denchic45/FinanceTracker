package com.denchic45.financetracker.domain.usecase

import arrow.core.Ior
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.domain.model.AccountItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ObserveAccountByIdUseCase(private val accountRepository: AccountRepository) {
    operator fun invoke(accountId: UUID): Flow<Ior<Failure, AccountItem?>> {
        return accountRepository.observeById(accountId)
    }
}