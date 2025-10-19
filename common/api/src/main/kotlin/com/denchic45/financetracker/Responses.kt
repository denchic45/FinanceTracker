package com.denchic45.financetracker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Алиас модели ответа с бэкенда */
typealias BackendResponseResult<T> = ResponseResult<T, BackendErrorResponse>

/** Модель ошибки бэкенда */
@Serializable
data class BackendErrorResponse(val reason: String) : ErrorResponse

@Serializable
object UnknownErrorResponse : ErrorResponse

/** Алиас модели ответа с успешным результатом или ошибкой */
typealias ResponseResult<T, E> = Result<T, E>

/** Алиас модели ответа с успешным пустым результатом или ошибкой */
typealias EmptyBackendResponseResult = Result<Unit, BackendErrorResponse>

suspend inline fun <reified T : Any, reified E : ErrorResponse> HttpResponse.toResult(): ResponseResult<T, E> {
    return if (status.isSuccess()) {
        Ok(body())
    } else {
        println("printed error: ${bodyAsText()}")
        Err(body<E>())
    }
}

suspend inline fun <reified E : ErrorResponse> HttpResponse.toResultText(): ResponseResult<String, E> {
    return if (status.isSuccess()) {
        Ok(bodyAsText())
    } else {
        println("printed error: ${bodyAsText()}")
        Err(body<E>())
    }
}

suspend inline fun <reified T : Any?, reified E : ErrorResponse> HttpResponse.toNullableResult(): Result<T?, E> {
    return if (status.isSuccess()) {
        Ok(bodyOrNull<T>())
    } else {
        println("printed error: ${bodyAsText()}")
        Err(body<E>())
    }
}

suspend inline fun <reified T : Any?> HttpResponse.bodyNullable() = call.bodyNullable(typeInfo<T>()) as T

interface ErrorResponse

suspend inline fun <reified T, reified E> HttpResponse.toResult(result: (HttpResponse) -> T): ResponseResult<T, E> {
    return if (status.isSuccess()) {
        Ok(result(this))
    } else {
        Err(body())
    }
}

suspend inline fun <reified T> HttpResponse.bodyOrNull(): T? {
    return try {
        val text = bodyAsText()
        Json.decodeFromString<T>(text)
    } catch (e: Exception) {
        null
    }
}