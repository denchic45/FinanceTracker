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

    fun add(request: AbstractTransactionRequest): Either<ApiError, AbstractTransactionResponse> = either {
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
                        tags = getTagsByIds(request.tagIds)
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
            }.toResponse()
        }
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
                        tags = getTagsByIds(request.tagIds)
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

    private fun Raise<TagNotFound>.getTagsByIds(tagIds: List<Long>): SizedIterable<TagDao> {
        return ensureNotNull(
            TagDao.find(Tags.id inList tagIds).takeIf { it.count().toInt() == tagIds.size }) { TagNotFound }
    }

    fun find(userId: UUID, page: Int, pageSize: Int) = either {
        transaction {
            ensure(UserDao.exists(userId)) { UserNotFound }
            val query = Transactions.innerJoin(Accounts, { Transactions.sourceAccountId }, { Accounts.id }).selectAll()
                .where(Accounts.ownerId eq userId).orderBy(Transactions.datetime, SortOrder.DESC)

            val count = query.count()

            val transactions = TransactionDao.wrapRows(
                query.limit(pageSize).offset((page - 1).toLong() * pageSize)
            ).toTransactionResponses()

            PagingResponse(
                results = transactions,
                page = page,
                count = pageSize,
                totalPages = ceil(count / pageSize.toDouble()).toInt()
            )
        }
    }

    fun findById(transactionId: Long): Either<TransactionNotFound, AbstractTransactionResponse> = transaction {
        TransactionDao.findById(transactionId)?.toResponse()?.right() ?: TransactionNotFound.left()
    }

    fun remove(transactionId: Long): Option<TransactionNotFound> = transaction {
        TransactionDao.findById(transactionId)?.delete()?.right() ?: return@transaction TransactionNotFound.some()
        none()
    }
}