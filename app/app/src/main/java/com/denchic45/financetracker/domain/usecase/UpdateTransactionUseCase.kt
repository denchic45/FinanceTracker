package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.api.transaction.model.AbstractTransactionRequest
import com.denchic45.financetracker.api.transaction.model.AbstractTransactionResponse
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.repository.TransactionRepository

class UpdateTransactionUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(
        transactionId: Long,
        request: AbstractTransactionRequest
    ): RequestResult<AbstractTransactionResponse> {
        return transactionRepository.update(transactionId, request)
    }
}