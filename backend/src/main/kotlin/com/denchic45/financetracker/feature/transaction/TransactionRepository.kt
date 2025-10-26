package com.denchic45.financetracker.feature.transaction

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.right
import arrow.core.some
import com.denchic45.financetracker.database.table.AccountDao
import com.denchic45.financetracker.database.table.CategoryDao
import com.denchic45.financetracker.database.table.TransactionDao
import com.denchic45.financetracker.database.table.Transactions
import com.denchic45.financetracker.error.AccountNotFound
import com.denchic45.financetracker.error.CategoryNotFound
import com.denchic45.financetracker.error.DomainError
import com.denchic45.financetracker.error.TransactionNotFound
import com.denchic45.financetracker.transaction.model.TransactionRequest
import com.denchic45.financetracker.transaction.model.TransactionResponse
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository() {

    fun add(request: TransactionRequest): Either<DomainError, TransactionResponse> = either {
        transaction {
            TransactionDao.new {
                datetime = request.datetime
                amount = request.amount
                type = request.type
                description = request.description
                account = ensureNotNull(AccountDao.findById(request.accountId)) { AccountNotFound }
                category = ensureNotNull(CategoryDao.findById(request.categoryId)) { CategoryNotFound }
                incomeAccount = request.incomeSourceId?.let {
                    ensureNotNull(AccountDao.findById(it)) { AccountNotFound }
                }
            }.toResponse()
        }
    }

    fun find(page: Int, pageSize: Int) = transaction {
        TransactionDao.wrapRows(
            Transactions.selectAll()
                .limit(pageSize)
                .offset((page - 1).toLong() * pageSize)
        ).toTransactionResponses()
    }

    fun findById(transactionId: Long): Either<TransactionNotFound, TransactionResponse> = transaction {
        TransactionDao.findById(transactionId)?.toResponse()?.right() ?: TransactionNotFound.left()
    }

    fun update(
        transactionId: Long,
        request: TransactionRequest
    ): Either<TransactionNotFound, TransactionResponse> = transaction {
        TransactionDao.findById(transactionId)?.apply {
            datetime = request.datetime
            amount = request.amount
            type = request.type
            description = request.description
            account = AccountDao[request.accountId]
            category = CategoryDao[request.categoryId]
            incomeAccount = request.incomeSourceId?.let { AccountDao[it] }
        }?.toResponse()?.right() ?: TransactionNotFound.left()
    }

    fun remove(transactionId: Long): Option<TransactionNotFound> = transaction {
        TransactionDao.findById(transactionId)
            ?.delete()?.right()
            ?: return@transaction TransactionNotFound.some()
        None
    }
}