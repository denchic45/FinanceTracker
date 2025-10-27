package com.denchic45.financetracker.transaction

import com.denchic45.financetracker.response.ResponseResult
import com.denchic45.financetracker.response.EmptyResponseResult
import com.denchic45.financetracker.response.safeApiCall
import com.denchic45.financetracker.response.safeApiCallForEmpty
import com.denchic45.financetracker.transaction.model.AbstractTransactionRequest
import com.denchic45.financetracker.transaction.model.AbstractTransactionResponse
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

    suspend fun add(request: AbstractTransactionRequest): ResponseResult<AbstractTransactionResponse> {
        return client.post("/transactions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getList(page: Int, pageSize: Int): ResponseResult<PagingResponse<AbstractTransactionResponse>> {
        return client.safeApiCall {
            get("/transactions") {
                parameter("page", page)
                parameter("pageSize", pageSize)
            }
        }
    }

    suspend fun getById(transactionId: Long): ResponseResult<AbstractTransactionResponse> {
        return client.safeApiCall { get("/transactions/$transactionId") }
    }

    suspend fun update(
        transactionId: Long,
        request: AbstractTransactionRequest
    ): ResponseResult<AbstractTransactionResponse> {
        return client.safeApiCall {
            put("/transactions/$transactionId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun delete(transactionId: Long): EmptyResponseResult {
        return client.safeApiCallForEmpty { delete("/transactions/$transactionId") }
    }
}