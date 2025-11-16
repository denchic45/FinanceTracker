package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.repository.TransactionRepository

class RemoveTransactionUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(transactionId: Long): EmptyRequestResult {
        return transactionRepository.remove(transactionId)
    }
}