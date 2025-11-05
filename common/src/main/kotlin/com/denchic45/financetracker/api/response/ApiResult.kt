package com.denchic45.financetracker.api.response

import arrow.core.*
import com.denchic45.financetracker.api.error.ApiError
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.serialization.json.Json

typealias ApiResult<T> = Either<ApiError, T>

typealias EmptyApiResult = Option<ApiError>

suspend inline fun <reified T> HttpResponse.toResult(): ApiResult<T> {
    return if (status.isSuccess()) {
        body<T>().right()
    } else {
        try {
            body<ApiError>().left()
        } catch (_: NoTransformationFoundException) {
            throw UnknownApiException(
                status.value, bodyAsText()
            )
        }
    }
}

suspend inline fun HttpResponse.toEmptyResult(): EmptyApiResult {
    return if (status.isSuccess()) {
        none()
    } else {
        try {
            body<ApiError>().some()
        } catch (_: NoTransformationFoundException) {
            throw UnknownApiException(
                status.value, bodyAsText()
            )
        }
    }
}

suspend inline fun <reified T : Any?> HttpResponse.bodyNullable(): T? {
    return call.bodyNullable(typeInfo<T>()) as T
}

suspend inline fun <reified T> HttpResponse.bodyOrNull(): T? {
    return try {
        val text = bodyAsText()
        Json.decodeFromString<T>(text)
    } catch (e: Exception) {
        null
    }
}

class UnknownApiException(val code: Int, message: String) : Exception(message)