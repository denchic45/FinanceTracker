package com.denchic45.financetracker.api.account

import com.denchic45.financetracker.api.account.model.AccountRequest
import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.api.response.ApiResult
import com.denchic45.financetracker.api.response.EmptyApiResult
import com.denchic45.financetracker.api.response.toEmptyResult
import com.denchic45.financetracker.api.response.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class AccountApi(private val client: HttpClient) {

    suspend fun create(request: AccountRequest): ApiResult<AccountResponse> {
        return client.post("/accounts") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun getList(): ApiResult<List<AccountResponse>> {
        return client.get("/accounts").toResult()
    }

    suspend fun getById(accountId: UUID): ApiResult<AccountResponse> {
        return client.get("/accounts/$accountId").toResult()
    }

    suspend fun update(accountId: UUID, request: AccountRequest): ApiResult<AccountResponse> {
        return client.put("/accounts/$accountId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun delete(accountId: UUID): EmptyApiResult {
        return  client.delete("/accounts/$accountId").toEmptyResult()
    }
}