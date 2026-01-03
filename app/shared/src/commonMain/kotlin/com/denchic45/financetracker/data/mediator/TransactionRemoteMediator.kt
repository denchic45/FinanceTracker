package com.denchic45.financetracker.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import arrow.core.getOrElse
import com.denchic45.financetracker.api.transaction.TransactionApi
import com.denchic45.financetracker.api.transaction.model.AbstractTransactionResponse
import com.denchic45.financetracker.data.asThrowable
import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.safeFetch

@OptIn(ExperimentalPagingApi::class)
class TransactionRemoteMediator(
    private val transactionApi: TransactionApi,
    private val onUpsert: suspend (List<AbstractTransactionResponse>) -> Unit
) : RemoteMediator<Int, AggregatedTransactionEntity>() {

    private var page = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AggregatedTransactionEntity>
    ): MediatorResult {
        page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()

                if (lastItem == null) 1
                else page + 1
            }
        }
        val transactions = safeFetch { transactionApi.getList(page, state.config.pageSize) }
            .getOrElse {
                return MediatorResult.Error(it.asThrowable())
            }

        onUpsert(transactions.results)
        return MediatorResult.Success(transactions.totalPages == page)
    }
}