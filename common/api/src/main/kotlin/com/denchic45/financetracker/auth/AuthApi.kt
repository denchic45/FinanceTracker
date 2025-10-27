package com.denchic45.financetracker.auth

import com.denchic45.financetracker.auth.model.SignInRequest
import com.denchic45.financetracker.auth.model.SignUpRequest
import com.denchic45.financetracker.response.ResponseResult
import com.denchic45.financetracker.response.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {

    suspend fun signIn(request: SignInRequest): ResponseResult<String> {
        return client.safeApiCall {
            post("/auth/token") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun signUp(request: SignUpRequest): ResponseResult<String> {
        return client.safeApiCall {
            post("/auth/sign-up") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }
}