package com.denchic45.financetracker.ktor

import com.denchic45.financetracker.util.toUUID
import io.ktor.http.Parameters
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.util.getOrFail
import java.util.UUID


fun Parameters.getUuid(name: String): UUID? = try {
    get(name)?.tryToUUID()
} catch (e: MissingRequestParameterException) {
    throw e
}

fun Parameters.getUuidOrFail(name: String): UUID = try {
    getOrFail(name).tryToUUID()
} catch (e: MissingRequestParameterException) {
    throw e
}

inline fun <reified T : Enum<T>> Parameters.getEnum(name: String): T? {
    return get(name)?.let<String, T>(::enumValueOf)
}

inline fun <reified T : Enum<T>> Parameters.getEnumOrFail(name: String): T {
    return enumValueOf(getOrFail(name))
}

fun String.tryToUUID(): UUID = try {
    toUUID()
} catch (t: Throwable) {
    throw BadRequestException("Invalid id")
}