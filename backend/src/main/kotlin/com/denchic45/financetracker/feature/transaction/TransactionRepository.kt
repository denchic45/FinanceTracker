package com.denchic45.financetracker.feature.transaction

import arrow.core.*
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.denchic45.financetracker.database.table.AccountDao
import com.denchic45.financetracker.database.table.CategoryDao
import com.denchic45.financetracker.database.table.TransactionDao
import com.denchic45.financetracker.database.table.Transactions
import com.denchic45.financetracker.error.AccountNotFound
import com.denchic45.financetracker.error.CategoryNotFound
import com.denchic45.financetracker.error.DomainError
import com.denchic45.financetracker.error.TransactionNotFound
import com.denchic45.financetracker.transaction.model.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository() {

    fun add(request: AbstractTransactionRequest): Either<DomainError, AbstractTransactionResponse> = either {
        transaction {
            TransactionDao.new {
                datetime = request.datetime
                amount = request.amount
                description = request.note
                account = ensureNotNull(AccountDao.findById(request.accountId)) { AccountNotFound }
                when (request) {
                    is TransactionRequest -> {
                        val categoryDao = ensureNotNull(CategoryDao.findById(request.categoryId)) { CategoryNotFound }
                        type = if (request.income) TransactionType.INCOME else TransactionType.EXPENSE
                        category = categoryDao
                        incomeAccount = null
                    }

                    is TransferTransactionRequest -> {
                        val incomeAccountDao = ensureNotNull(
                            AccountDao.findById(request.incomeSourceId)
                        ) { AccountNotFound }
                        type = TransactionType.TRANSFER
                        category = null
                        incomeAccount = incomeAccountDao
                    }
                }
            }
        }.toResponse()
    }

    fun find(page: Int, pageSize: Int) = transaction {
        TransactionDao.wrapRows(
            Transactions.selectAll()
                .limit(pageSize)
                .offset((page - 1).toLong() * pageSize)
        ).toTransactionResponses()
    }

    fun findById(transactionId: Long): Either<TransactionNotFound, AbstractTransactionResponse> = transaction {
        TransactionDao.findById(transactionId)?.toResponse()?.right() ?: TransactionNotFound.left()
    }

    fun update(
        transactionId: Long,
        request: AbstractTransactionRequest
    ) = either {
        transaction {
            TransactionDao.findById(transactionId)?.apply {
                datetime = request.datetime
                amount = request.amount
                description = request.note
                account = ensureNotNull(AccountDao.findById(request.accountId)) { AccountNotFound }

                when (request) {
                    is TransactionRequest -> {
                        type = if (request.income) TransactionType.INCOME else TransactionType.EXPENSE
                        category = ensureNotNull(CategoryDao.findById(request.categoryId)) { CategoryNotFound }
                        incomeAccount = null
                    }

                    is TransferTransactionRequest -> {
                        type = TransactionType.TRANSFER
                        category = null
                        incomeAccount = ensureNotNull(
                            AccountDao.findById(request.incomeSourceId)
                        ) { AccountNotFound }
                    }
                }
            }?.toResponse() ?: raise(TransactionNotFound)
        }
    }

    fun remove(transactionId: Long): Option<TransactionNotFound> = transaction {
        TransactionDao.findById(transactionId)
            ?.delete()?.right()
            ?: return@transaction TransactionNotFound.some()
        none()
    }
}