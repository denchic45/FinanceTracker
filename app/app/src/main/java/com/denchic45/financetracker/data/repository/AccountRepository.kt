package com.denchic45.financetracker.data.repository

import com.denchic45.financetracker.account.AccountApi
import com.denchic45.financetracker.account.model.AccountRequest
import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.data.fetchResourceFlow
import com.denchic45.financetracker.domain.Resource
import kotlinx.coroutines.flow.Flow

class AccountRepository(private val accountApi: AccountApi) {
    fun findAll(): Flow<Resource<List<AccountResponse>>> {
        return fetchResourceFlow { accountApi.getList() }
    }

    suspend fun add(request: AccountRequest): AccountResponse {
        return accountApi.add(request)
    }


    suspend fun update(accountId: UUID, request: AccountRequest): ResponseResult<AccountResponse> {
        return accountApi.update(accountId, request)
    }

    suspend fun remove(accountId: UUID): EmptyResponseResult {
        return accountApi.delete(accountId)
    }
}