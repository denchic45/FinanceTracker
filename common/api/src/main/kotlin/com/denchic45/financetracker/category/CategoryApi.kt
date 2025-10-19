package com.denchic45.financetracker.category

import com.denchic45.financetracker.category.model.CategoryRequest
import com.denchic45.financetracker.category.model.CategoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class CategoryApi(private val client: HttpClient) {

    suspend fun add(request: CategoryRequest): CategoryResponse {
        return client.post("/categories") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getList(): List<CategoryResponse> {
        return client.get("/categories").body()
    }

    suspend fun getById(categoryId: Long): CategoryResponse {
        return client.get("/categories/$categoryId").body()
    }

    suspend fun update(categoryId: Long, request: CategoryRequest): CategoryResponse {
        return client.put("/categories/$categoryId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun delete(categoryId: Long) {
        return client.delete("/categories/$categoryId").body()
    }
}