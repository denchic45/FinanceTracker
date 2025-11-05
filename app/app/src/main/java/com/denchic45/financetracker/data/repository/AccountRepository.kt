package com.denchic45.financetracker.data.repository

import arrow.core.Ior
import com.denchic45.financetracker.api.account.AccountApi
import com.denchic45.financetracker.api.account.model.AccountRequest
import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.entity.AccountEntity
import com.denchic45.financetracker.data.mapper.toAccountEntities
import com.denchic45.financetracker.data.mapper.toAccountItems
import com.denchic45.financetracker.data.observeData
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.api.response.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class AccountRepository(
    private val accountApi: AccountApi,
    private val database: AppDatabase,
    private val accountDao: AccountDao
) {
    fun observeAll(): Flow<Ior<Failure, List<AccountItem>>> {
        return observeData(
            query = accountDao.observeAll().map(List<AccountEntity>::toAccountItems),
            fetch = {
                safeFetch { accountApi.getList() }.onRight {
                    accountDao.upsert(it.toAccountEntities())
                }
            })
    }

    suspend fun add(request: AccountRequest): RequestResult<AccountResponse> {
        return safeFetch { accountApi.create(request) }
    }


    suspend fun update(accountId: UUID, request: AccountRequest): ApiResult<AccountResponse> {
        return accountApi.update(accountId, request)
    }

    suspend fun remove(accountId: UUID): EmptyRequestResult {
        return safeFetchForEmptyResult { accountApi.delete(accountId) }
    }
}