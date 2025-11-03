package com.denchic45.financetracker.tag

import com.denchic45.financetracker.response.ApiResult
import com.denchic45.financetracker.response.EmptyApiResult
import com.denchic45.financetracker.response.toEmptyResult
import com.denchic45.financetracker.response.toResult
import com.denchic45.financetracker.tag.model.TagRequest
import com.denchic45.financetracker.tag.model.TagResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class TagApi(private val client: HttpClient) {

    suspend fun create(request: TagRequest): ApiResult<TagResponse> {
        return client.post("/tags") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun getList(): ApiResult<List<TagResponse>> {
        return client.get("/tags").toResult()
    }

    suspend fun getById(tagId: Long): ApiResult<TagResponse> {
        return client.get("/tags/$tagId").toResult()
    }

    suspend fun update(tagId: Long, request: TagRequest): ApiResult<TagResponse> {
        return client.put("/tags/$tagId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun delete(tagId: Long): EmptyApiResult {
        return client.delete("/tags/$tagId").toEmptyResult()
    }
}