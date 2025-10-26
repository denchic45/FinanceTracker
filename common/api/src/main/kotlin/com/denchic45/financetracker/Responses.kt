package com.denchic45.financetracker

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.json.Json

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