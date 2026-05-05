package com.denchic45.financetracker.feature.transaction

import arrow.core.*
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.denchic45.financetracker.api.PagingResponse
import com.denchic45.financetracker.api.error.*
import com.denchic45.financetracker.api.transaction.model.*
import com.denchic45.financetracker.database.table.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.dao.with
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.SizedIterable
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.math.ceil
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TransactionRepository {

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

    fun find(userId: Uuid, filters: TransactionFilters) = either {
        transaction {
            ensure(UserDao.exists(userId)) { UserNotFound }
            val pageSize = filters.pageSize
            val page = filters.page

            val query = Transactions.innerJoin(Accounts, { Transactions.sourceAccountId }, { Accounts.id }).selectAll()
                .where(Accounts.ownerId eq userId)
                .orderBy(filters.sortBy.column to filters.sortOrder)
                .applyFilters(userId, filters)

            val count = query.count().toInt()

            val transactionEntities = TransactionDao.wrapRows(
                query.limit(pageSize).offset((page - 1).toLong() * pageSize)
            ).with(TransactionDao::account, TransactionDao::category, TransactionDao::tags)

            val accountIds = buildSet {
                transactionEntities.forEach { dao ->
                    add(dao.account.id.value)
                    dao.incomeAccount?.let { add(it.id.value) }
                }
            }

            val accounts = AccountDao.getByIds(accountIds).toTransactionAccounts()

            PagingResponse(
                results = transactionEntities.toTransactionResponses(accounts),
                page = page,
                count = count,
                totalPages = ceil(count / pageSize.toDouble()).toInt()
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun Query.applyFilters(userId: Uuid, filters: TransactionFilters): Query {
        val query = this

        query.adjustWhere {
            // Базовое условие: только транзакции текущего пользователя
            // (через JOIN с таблицей Accounts)
            var conditions: Op<Boolean> = Accounts.ownerId eq userId

            // 1. Фильтр по датам
            filters.fromDate?.let {
                conditions = conditions and (Transactions.datetime greaterEq it.atTime(LocalTime(0, 0)))
            }
            filters.toDate?.let {
                // plusDays(1) гарантирует, что мы включим весь последний день
                conditions = conditions and (
                        Transactions.datetime less it.plus(1, DateTimeUnit.DAY)
                            .atTime(LocalTime(0, 0))
                        )
            }

            // 2. Фильтр по аккаунтам (множественный выбор)
            filters.accountIds?.takeIf { it.isNotEmpty() }?.let { ids ->
                conditions = conditions and (Transactions.sourceAccountId inList ids)
            }

            // 3. Фильтр по категориям (множественный выбор)
            filters.categoryIds?.takeIf { it.isNotEmpty() }?.let { ids ->
                conditions = conditions and (Transactions.categoryId inList ids)
            }

            // 4. Фильтр по тегам (через EXISTS, чтобы избежать дублей строк)
            filters.tagIds?.takeIf { it.isNotEmpty() }?.let { ids ->
                conditions = conditions and exists(
                    TransactionTags.select(TransactionTags.id)
                        .where {
                            (TransactionTags.transactionId eq Transactions.id) and
                                    (TransactionTags.tagId inList ids)
                        }
                )
            }

            conditions
        }

        return query
    }

    fun findById(transactionId: Long): Either<TransactionNotFound, AbstractTransactionResponse> = transaction {
        TransactionDao.findById(transactionId)?.toResponse()?.right()
            ?: TransactionNotFound.left()
    }

    fun remove(transactionId: Long): Option<TransactionNotFound> = transaction {
        TransactionDao.findById(transactionId)?.delete()?.right() ?: return@transaction TransactionNotFound.some()
        none()
    }
}