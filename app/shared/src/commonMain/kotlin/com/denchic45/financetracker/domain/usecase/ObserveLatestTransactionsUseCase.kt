package com.denchic45.financetracker.domain.usecase

import arrow.core.Ior
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.repository.TransactionRepository
import com.denchic45.financetracker.domain.model.TransactionItem
import kotlinx.coroutines.flow.Flow

class ObserveLatestTransactionsUseCase(private val transactionRepository: TransactionRepository) {
    operator fun invoke(limit: Int = 5): Flow<Ior<Failure, List<TransactionItem>>> {
        return transactionRepository.findLatest(limit)
    }
}