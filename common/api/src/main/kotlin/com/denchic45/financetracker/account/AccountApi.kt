package com.denchic45.financetracker.account

import com.denchic45.financetracker.account.model.AccountRequest
import com.denchic45.financetracker.account.model.AccountResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AccountApi(private val client: HttpClient) {

    suspend fun add(request: AccountRequest): AccountResponse {
        return client.post("/accounts") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getList(): List<AccountResponse> {
        return client.get("/accounts").body()
    }

    suspend fun getById(accountId: Long): AccountResponse {
        return client.get("/accounts/$accountId").body()
    }

    suspend fun update(accountId: Long, request: AccountRequest): AccountResponse {
        return client.put("/accounts/$accountId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun delete(accountId: Long) {
        return client.delete("/accounts/$accountId").body()
    }
}