package com.denchic45.financetracker.api.auth

import com.denchic45.financetracker.api.auth.model.AuthResponse
import com.denchic45.financetracker.api.auth.model.RefreshTokenRequest
import com.denchic45.financetracker.api.auth.model.SignInRequest
import com.denchic45.financetracker.api.auth.model.SignUpRequest
import com.denchic45.financetracker.api.response.ApiResult
import com.denchic45.financetracker.api.response.toResult
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {

    suspend fun signUp(request: SignUpRequest): ApiResult<AuthResponse> {
        return client.post("/auth/sign-up") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    suspend fun signIn(request: SignInRequest): ApiResult<AuthResponse> {
        return client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(request)
            parameter("grant_type", "password")
        }.toResult()
    }

    suspend fun refreshToken(request: RefreshTokenRequest): ApiResult<AuthResponse> {
        return client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(request)
            parameter("grant_type", "refresh_token")
        }.toResult()
    }
}