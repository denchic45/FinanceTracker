package com.denchic45.financetracker.account

import com.denchic45.financetracker.account.model.AccountRequest
import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.response.ResponseResult
import com.denchic45.financetracker.response.EmptyResponseResult
import com.denchic45.financetracker.response.safeApiCall
import com.denchic45.financetracker.response.safeApiCallForEmpty
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

class AccountApi(private val client: HttpClient) {

    suspend fun add(request: AccountRequest): ResponseResult<AccountResponse> {
        return client.safeApiCall {
            client.post("/accounts") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun getList(): ResponseResult<List<AccountResponse>> {
        return client.safeApiCall { client.get("/accounts") }
    }

    suspend fun getById(accountId: UUID): ResponseResult<AccountResponse> {
        return client.safeApiCall { client.get("/accounts/$accountId") }
    }

    suspend fun update(accountId: UUID, request: AccountRequest): ResponseResult<AccountResponse> {
        return client.safeApiCall {
            client.put("/accounts/$accountId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun delete(accountId: UUID): EmptyResponseResult {
        return client.safeApiCallForEmpty { client.delete("/accounts/$accountId") }
    }
}