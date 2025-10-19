package com.denchic45.financetracker.util

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpClient.safeApiCall(
    request: suspend HttpClient.() -> HttpResponse,
): T {
    return safeApiCall(request) { body() }
}

suspend inline fun <reified T> HttpClient.safeApiCall(
    request: suspend HttpClient.() -> HttpResponse,
    map: HttpResponse.() -> T
): T {
    val response = request()
    return if (response.status.isSuccess()) {
        map(response)
    } else throw ApiException(response.status.value, response.bodyAsText())
}