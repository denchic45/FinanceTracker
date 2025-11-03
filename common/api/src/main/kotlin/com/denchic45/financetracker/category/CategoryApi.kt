package com.denchic45.financetracker.category

import com.denchic45.financetracker.category.model.CreateCategoryRequest
import com.denchic45.financetracker.category.model.CategoryResponse
import com.denchic45.financetracker.category.model.UpdateCategoryRequest
import com.denchic45.financetracker.response.ApiResult
import com.denchic45.financetracker.response.EmptyApiResult
import com.denchic45.financetracker.response.toEmptyResult
import com.denchic45.financetracker.response.toResult
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class CategoryApi(private val client: HttpClient) {
    suspend fun create(request: CreateCategoryRequest): ApiResult<CategoryResponse> {
        return client.post("/categories") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun getList(): ApiResult<List<CategoryResponse>> {
        return client.get("/categories").toResult()
    }

    suspend fun getById(categoryId: Long): ApiResult<CategoryResponse> {
        return client.get("/categories/$categoryId").toResult()
    }

    suspend fun update(categoryId: Long, request: UpdateCategoryRequest): ApiResult<CategoryResponse> {
        return client.put("/categories/$categoryId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun delete(categoryId: Long): EmptyApiResult {
        return client.delete("/categories/$categoryId").toEmptyResult()
    }
}