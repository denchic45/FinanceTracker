package com.denchic45.financetracker.domain.usecase

import androidx.paging.PagingData
import com.denchic45.financetracker.data.repository.TransactionRepository
import com.denchic45.financetracker.domain.model.TransactionItem
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsUseCase(private val transactionRepository: TransactionRepository) {
    operator fun invoke(): Flow<PagingData<TransactionItem>> {
        return transactionRepository.find()
    }
}