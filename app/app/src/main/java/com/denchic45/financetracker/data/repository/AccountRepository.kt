package com.denchic45.financetracker.data.repository

import arrow.core.Ior
import com.denchic45.financetracker.account.AccountApi
import com.denchic45.financetracker.account.model.AccountRequest
import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.entity.AccountEntity
import com.denchic45.financetracker.data.mapper.toAccountEntities
import com.denchic45.financetracker.data.mapper.toAccountResponses
import com.denchic45.financetracker.data.observeData
import com.denchic45.financetracker.response.EmptyResponseResult
import com.denchic45.financetracker.response.Failure
import com.denchic45.financetracker.response.ResponseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class AccountRepository(
    private val accountApi: AccountApi,
    private val database: AppDatabase,
    private val accountDao: AccountDao
) {
    fun findAll(): Flow<Ior<Failure, List<AccountResponse>>> {
        return observeData(
            query = accountDao.observeAll().map(List<AccountEntity>::toAccountResponses),
            fetch = {
                accountApi.getList().onRight {
                    accountDao.upsert(it.toAccountEntities())
                }
            })
    }

    suspend fun add(request: AccountRequest): ResponseResult<AccountResponse> {
        return accountApi.add(request)
    }


    suspend fun update(accountId: UUID, request: AccountRequest): ResponseResult<AccountResponse> {
        return accountApi.update(accountId, request)
    }

    suspend fun remove(accountId: UUID): EmptyResponseResult {
        return accountApi.delete(accountId)
    }
}