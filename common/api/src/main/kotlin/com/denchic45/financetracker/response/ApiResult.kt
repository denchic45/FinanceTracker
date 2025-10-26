package com.denchic45.financetracker.response

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import arrow.core.some
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

typealias ResponseResult<T> = Either<Failure, T>

typealias EmptyResponseResult = Option<Failure>

suspend inline fun <reified T> HttpClient.safeApiCall(
    request: suspend HttpClient.() -> HttpResponse,
): ResponseResult<T> {
    return safeApiCall(request) { body() }
}

suspend inline fun <reified T> HttpClient.safeApiCall(
    request: suspend HttpClient.() -> HttpResponse,
    map: HttpResponse.() -> T
): ResponseResult<T> {
    try {
        val response = request()
        return if (response.status.isSuccess()) {
            map(response).right()
        } else {
            response.asFailure().left()
        }
    } catch (t: Throwable) {
        return t.asFailure().left()
    }
}

suspend inline fun HttpClient.safeApiCallForEmpty(
    request: suspend HttpClient.() -> HttpResponse,
): EmptyResponseResult {
    try {
        val response = request()
        return if (response.status.isSuccess()) {
            None
        } else {
            response.asFailure().some()
        }
    } catch (t: Throwable) {
        return t.asFailure().some()
    }
}

fun <T> Either<Failure, T>.toOption(): Option<Failure> = fold(
    ifLeft = { it.some() },
    ifRight = { None }
)