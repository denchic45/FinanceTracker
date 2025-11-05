package com.denchic45.financetracker.api.transaction

import com.denchic45.financetracker.api.response.ApiResult
import com.denchic45.financetracker.api.response.EmptyApiResult
import com.denchic45.financetracker.api.response.toEmptyResult
import com.denchic45.financetracker.api.response.toResult
import com.denchic45.financetracker.api.transaction.model.AbstractTransactionRequest
import com.denchic45.financetracker.api.transaction.model.AbstractTransactionResponse
import com.denchic45.financetracker.api.PagingResponse
import com.denchic45.financetracker.api.response.toEmptyResult
import com.denchic45.financetracker.api.response.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class TransactionApi(private val client: HttpClient) {

    suspend fun create(request: AbstractTransactionRequest): ApiResult<AbstractTransactionResponse> {
        return client.post("/transactions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun getList(page: Int, pageSize: Int): ApiResult<PagingResponse<AbstractTransactionResponse>> {
        return client.get("/transactions") {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.toResult()
    }

    suspend fun getById(transactionId: Long): ApiResult<AbstractTransactionResponse> {
        return client.get("/transactions/$transactionId").toResult()
    }

    suspend fun update(
        transactionId: Long,
        request: AbstractTransactionRequest
    ): ApiResult<AbstractTransactionResponse> {
        return client.put("/transactions/$transactionId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun delete(transactionId: Long): EmptyApiResult {
        return client.delete("/transactions/$transactionId").toEmptyResult()
    }
}