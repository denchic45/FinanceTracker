package com.denchic45.financetracker.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.denchic45.financetracker.data.TransactionRemoteMediator
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.dao.CategoryDao
import com.denchic45.financetracker.data.database.dao.TransactionDao
import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.fetchResourceFlow
import com.denchic45.financetracker.data.mapper.toAccountEntity
import com.denchic45.financetracker.data.mapper.toCategoryEntity
import com.denchic45.financetracker.data.mapper.toTransactionEntity
import com.denchic45.financetracker.data.mapper.toTransactionItem
import com.denchic45.financetracker.data.observeResource
import com.denchic45.financetracker.domain.Resource
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.transaction.model.TransactionApi
import com.denchic45.financetracker.transaction.model.TransactionRequest
import com.denchic45.financetracker.transaction.model.TransactionResponse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionApi: TransactionApi,
    private val database: AppDatabase,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao
) {

    @OptIn(ExperimentalPagingApi::class)
    fun find(): Flow<PagingData<TransactionItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 20),
            remoteMediator = TransactionRemoteMediator(transactionApi, database),
            pagingSourceFactory = { transactionDao.get() }
        ).flow.map { it.map(AggregatedTransactionEntity::toTransactionItem) }
    }

    fun findById(transactionId: Long) = observeResource(
        query = transactionDao.observeById(transactionId).map {

        },
        fetch = {
            coroutineScope {
                val transactionResponse = transactionApi.getById(transactionId)
                categoryDao.upsert(transactionResponse.category.toCategoryEntity())
                accountDao.upsert(transactionResponse.account.toAccountEntity())
                transactionDao.upsert(transactionResponse.toTransactionEntity())
            }
        }
    )

    fun add(request: TransactionRequest): Flow<Resource<TransactionResponse>> {
        return fetchResourceFlow { transactionApi.add(request) }
    }

    fun remove(transactionId: Long): Flow<Resource<Unit>> {
        return fetchResourceFlow { transactionApi.delete(transactionId) }
    }
}