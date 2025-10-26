package com.denchic45.financetracker.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.denchic45.financetracker.data.TransactionRemoteMediator
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.dao.CategoryDao
import com.denchic45.financetracker.data.database.dao.TransactionDao
import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.mapper.toAccountEntity
import com.denchic45.financetracker.data.mapper.toCategoryEntity
import com.denchic45.financetracker.data.mapper.toTransactionEntity
import com.denchic45.financetracker.data.mapper.toTransactionItem
import com.denchic45.financetracker.data.observeData
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.response.EmptyResponseResult
import com.denchic45.financetracker.response.ResponseResult
import com.denchic45.financetracker.transaction.TransactionApi
import com.denchic45.financetracker.transaction.model.TransactionRequest
import com.denchic45.financetracker.transaction.model.TransactionResponse
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

    fun findById(transactionId: Long) = observeData(
        query = transactionDao.observeById(transactionId)
            .map(AggregatedTransactionEntity::toTransactionItem),
        fetch = {
            val transactionResponse = transactionApi.getById(transactionId)
            transactionResponse.onRight { response ->
                database.withTransaction {
                    categoryDao.upsert(response.category.toCategoryEntity())
                    accountDao.upsert(response.account.toAccountEntity())
                    transactionDao.upsert(response.toTransactionEntity())
                }
            }
        }
    )

    suspend fun add(request: TransactionRequest): ResponseResult<TransactionResponse> {
        return transactionApi.add(request)
    }

    suspend fun remove(transactionId: Long): EmptyResponseResult {
        return transactionApi.delete(transactionId)
    }
}