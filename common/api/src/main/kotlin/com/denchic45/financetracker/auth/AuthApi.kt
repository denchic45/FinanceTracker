package com.denchic45.financetracker.auth

import com.denchic45.financetracker.auth.model.SignInRequest
import com.denchic45.financetracker.auth.model.SignUpRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi(private val client: HttpClient) {

    suspend fun signIn(request: SignInRequest): String {
        return client.post("/token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun signUp(request: SignUpRequest): String {
        return client.post("/sign-up") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}