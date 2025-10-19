package com.denchic45.financetracker.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.mapper.toTransactionEntities
import com.denchic45.financetracker.transaction.model.TransactionApi
import com.denchic45.financetracker.util.ApiException
import kotlinx.io.IOException

@OptIn(ExperimentalPagingApi::class)
class TransactionRemoteMediator(
    private val transactionApi: TransactionApi,
    private val database: AppDatabase
) : RemoteMediator<Int, AggregatedTransactionEntity>() {

    private var page = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AggregatedTransactionEntity>
    ): MediatorResult {
        return try {
            page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    if (lastItem == null) 1
                    else page + 1
                }
            }
            val transactions = transactionApi.getList(page, 30)

            val transactionDao = database.transactionDao
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    transactionDao.deleteAll()
                }
                transactionDao.upsert(transactions.results.toTransactionEntities())
            }
            MediatorResult.Success(transactions.info.next == null)

        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: ApiException) {
            MediatorResult.Error(exception)
        }
    }
}