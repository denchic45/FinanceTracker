package com.denchic45.financetracker.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.denchic45.financetracker.api.transaction.TransactionApi
import com.denchic45.financetracker.api.transaction.model.AbstractTransactionRequest
import com.denchic45.financetracker.api.transaction.model.AbstractTransactionResponse
import com.denchic45.financetracker.api.transaction.model.TransactionResponse
import com.denchic45.financetracker.api.transaction.model.TransferTransactionResponse
import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.dao.CategoryDao
import com.denchic45.financetracker.data.database.dao.TagDao
import com.denchic45.financetracker.data.database.dao.TransactionDao
import com.denchic45.financetracker.data.database.entity.AccountEntity
import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.database.entity.CategoryEntity
import com.denchic45.financetracker.data.database.entity.TagEntity
import com.denchic45.financetracker.data.database.entity.TransactionTagCrossRef
import com.denchic45.financetracker.data.mapper.toAccountEntity
import com.denchic45.financetracker.data.mapper.toCategoryEntity
import com.denchic45.financetracker.data.mapper.toTagEntities
import com.denchic45.financetracker.data.mapper.toTransactionEntity
import com.denchic45.financetracker.data.mapper.toTransactionItem
import com.denchic45.financetracker.data.mapper.toTransactionItems
import com.denchic45.financetracker.data.mediator.TransactionRemoteMediator
import com.denchic45.financetracker.data.observeData
import com.denchic45.financetracker.data.safeFetch
import com.denchic45.financetracker.data.safeFetchForEmptyResult
import com.denchic45.financetracker.domain.model.TransactionItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionApi: TransactionApi,
    private val database: AppDatabase,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val tagDao: TagDao
) {

    @OptIn(ExperimentalPagingApi::class)
    fun find(): Flow<PagingData<TransactionItem>> {
        return Pager(
            config = PagingConfig(pageSize = 30, initialLoadSize = 30),
            remoteMediator = TransactionRemoteMediator(
                transactionApi,
                ::upsertTransactions
            ),
            pagingSourceFactory = { transactionDao.observe() }
        ).flow.map { it.map(AggregatedTransactionEntity::toTransactionItem) }
    }

    fun findLatest(limit: Int) = observeData(
        query = transactionDao.getLatest(limit).distinctUntilChanged()
            .map { it.toTransactionItems() },
        fetch = {
            transactionApi.getList(1, limit).onRight { response ->
                upsertTransactions(response.results)
            }
        }
    )

    private suspend fun upsertTransactions(responses: List<AbstractTransactionResponse>) {
        val accounts = mutableSetOf<AccountEntity>()
        val categories = mutableSetOf<CategoryEntity>()
        val tags = mutableSetOf<TagEntity>()
        val tagsCrossRefs = mutableListOf<TransactionTagCrossRef>()
        val transactions = responses.map { transactionResponse ->
            accounts.add(transactionResponse.account.toAccountEntity())
            when (transactionResponse) {
                is TransactionResponse -> {
                    categories.add(transactionResponse.category.toCategoryEntity())
                    tags.addAll(transactionResponse.tags.toTagEntities())
                    tagsCrossRefs.addAll(transactionResponse.tags.map {
                        TransactionTagCrossRef(
                            transactionResponse.id,
                            it.id
                        )
                    })
                }

                is TransferTransactionResponse -> accounts.add(transactionResponse.incomeAccount.toAccountEntity())
            }
            transactionResponse.toTransactionEntity()
        }

        database.withTransaction {
            if (transactions.isNotEmpty()) {
                transactionDao.deleteByActualIdsAndDateRange(
                    transactions.map { it.id },
                    transactions.last().datetime,
                    transactions.first().datetime
                )
            } else transactionDao.deleteAll()

            categoryDao.upsert(categories)
            accountDao.upsert(accounts)
            tagDao.upsert(tags)
            transactionDao.upsert(transactions)
            tagDao.deleteByTransactionIds(transactions.map { it.id })
            tagDao.insertTransactionTags(tagsCrossRefs)
        }
    }

    fun observeById(transactionId: Long) = observeData(
        query = transactionDao.observeById(transactionId)
            .map { it?.toTransactionItem() },
        fetch = {
            transactionApi.getById(transactionId).onRight { response ->
                upsertTransaction(response)
            }
        }
    )


    suspend fun add(request: AbstractTransactionRequest): RequestResult<AbstractTransactionResponse> {
        return safeFetch {
            transactionApi.create(request)
        }.onRight { upsertTransaction(it) }
    }

    suspend fun update(
        transactionId: Long,
        request: AbstractTransactionRequest
    ): RequestResult<AbstractTransactionResponse> {
        return safeFetch {
            transactionApi.update(transactionId, request)
        }.onRight { upsertTransaction(it) }
    }

    private suspend fun upsertTransaction(response: AbstractTransactionResponse) {
        database.withTransaction {
            when (response) {
                is TransactionResponse -> {
                    tagDao.upsert(response.tags.toTagEntities())
                    categoryDao.upsert(response.category.toCategoryEntity())
                }

                is TransferTransactionResponse -> {
                    accountDao.upsert(response.incomeAccount.toAccountEntity())
                }
            }

            accountDao.upsert(response.account.toAccountEntity())
            transactionDao.upsert(response.toTransactionEntity())
            if (response is TransactionResponse) {
                tagDao.deleteByTransactionId(response.id)
                tagDao.insertTransactionTags(
                    response.tags.map {
                        TransactionTagCrossRef(response.id, it.id)
                    })
            }
        }
    }

    suspend fun remove(transactionId: Long): EmptyRequestResult {
        return safeFetchForEmptyResult { transactionApi.delete(transactionId) }
            .onNone {
                database.withTransaction { transactionDao.deleteById(transactionId) }
            }
    }
}