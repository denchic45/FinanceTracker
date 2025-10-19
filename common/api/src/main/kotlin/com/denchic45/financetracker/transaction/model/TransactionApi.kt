package com.denchic45.financetracker.transaction.model

import com.denchic45.financetracker.util.PagingResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TransactionApi(private val client: HttpClient) {

    suspend fun add(request: TransactionRequest): TransactionResponse {
        return client.post("/transactions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getList(page: Int, pageSize: Int): PagingResponse<TransactionResponse> {
        return client.get("/transactions") {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.body()
    }

    suspend fun getById(transactionId: Long): TransactionResponse {
        return client.get("/transactions/$transactionId").body()
    }

    suspend fun update(
        transactionId: Long,
        request: TransactionRequest
    ): TransactionResponse {
        return client.put("/transactions/$transactionId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun delete(transactionId: Long) {
        return client.delete("/transactions/$transactionId").body()
    }
}