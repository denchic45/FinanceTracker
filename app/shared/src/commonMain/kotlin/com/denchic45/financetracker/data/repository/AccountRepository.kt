package com.denchic45.financetracker.data.repository

import androidx.room.withTransaction
import arrow.core.Ior
import com.denchic45.financetracker.api.account.AccountApi
import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.api.account.model.CreateAccountRequest
import com.denchic45.financetracker.api.account.model.UpdateAccountRequest
import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.entity.AccountEntity
import com.denchic45.financetracker.data.mapper.toAccountEntities
import com.denchic45.financetracker.data.mapper.toAccountEntity
import com.denchic45.financetracker.data.mapper.toAccountItem
import com.denchic45.financetracker.data.mapper.toAccountItems
import com.denchic45.financetracker.data.observeData
import com.denchic45.financetracker.data.safeFetch
import com.denchic45.financetracker.data.safeFetchForEmptyResult
import com.denchic45.financetracker.domain.model.AccountItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.UUID

class AccountRepository(
    private val accountApi: AccountApi,
    private val database: AppDatabase,
    private val accountDao: AccountDao
) {
    fun observe(): Flow<Ior<Failure, List<AccountItem>>> = observeData(
        query = accountDao.observeAll().distinctUntilChanged()
           .map(List<AccountEntity>::toAccountItems),
        fetch = {
            accountApi.getList().onRight {
                database.withTransaction {
                    accountDao.upsert(it.toAccountEntities())
                }
            }
        },
        waitFetchResult = false
    )

    fun observeById(accountId: UUID): Flow<Ior<Failure, AccountItem?>> = observeData(
        query = accountDao.observeById(accountId).distinctUntilChanged()
            .map { it?.toAccountItem() },
        fetch = {
            accountApi.getById(accountId).onRight {
                upsertAccount(it)
            }
        }
    )

    suspend fun add(request: CreateAccountRequest): RequestResult<AccountResponse> {
        return safeFetch { accountApi.create(request) }
            .onRight { upsertAccount(it) }
    }


    suspend fun update(
        accountId: UUID,
        request: UpdateAccountRequest
    ): RequestResult<AccountResponse> {
        return safeFetch { accountApi.update(accountId, request) }
            .onRight { upsertAccount(it) }
    }

    private suspend fun upsertAccount(response: AccountResponse) {
        database.withTransaction {
            accountDao.upsert(response.toAccountEntity())
        }
    }

    suspend fun remove(accountId: UUID): EmptyRequestResult {
        return safeFetchForEmptyResult { accountApi.delete(accountId) }
            .onNone { database.withTransaction { accountDao.delete(accountId) } }
    }
}