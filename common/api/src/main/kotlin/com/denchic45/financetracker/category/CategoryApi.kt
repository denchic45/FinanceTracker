package com.denchic45.financetracker.category

import com.denchic45.financetracker.category.model.CategoryRequest
import com.denchic45.financetracker.category.model.CategoryResponse
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

class CategoryApi(private val client: HttpClient) {
    suspend fun add(request: CategoryRequest): ResponseResult<CategoryResponse> {
        return client.safeApiCall {
            post("/categories") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun getList(): ResponseResult<List<CategoryResponse>> {
        return client.safeApiCall { get("/categories") }
    }

    suspend fun getById(categoryId: Long): ResponseResult<CategoryResponse> {
        return client.safeApiCall { get("/categories/$categoryId") }
    }

    suspend fun update(categoryId: Long, request: CategoryRequest): ResponseResult<CategoryResponse> {
        return client.safeApiCall {
            put("/categories/$categoryId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun delete(categoryId: Long): EmptyResponseResult {
        return client.safeApiCallForEmpty {
            delete("/categories/$categoryId")
        }
    }
}