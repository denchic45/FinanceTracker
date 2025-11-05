package com.denchic45.financetracker.api.tag

import com.denchic45.financetracker.api.response.ApiResult
import com.denchic45.financetracker.api.response.EmptyApiResult
import com.denchic45.financetracker.api.response.toEmptyResult
import com.denchic45.financetracker.api.response.toResult
import com.denchic45.financetracker.api.tag.model.TagRequest
import com.denchic45.financetracker.api.tag.model.TagResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

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