package com.denchic45.financetracker.ktor

import com.denchic45.financetracker.util.toUuid
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.time.format.DateTimeParseException
import kotlin.uuid.Uuid


fun Parameters.getUuid(name: String): Uuid? {
    val value = get(name)
    return try {
        value?.toUuid()
    } catch (e: IllegalArgumentException) {
        throw ParameterConversionException(
            parameterName = name,
            type = "UUID",
            cause = e
        )
    }
}

fun Parameters.getUuidOrFail(name: String): Uuid {
    val value = getOrFail(name)
    return try {
        value.toUuid()
    } catch (e: IllegalArgumentException) {
        throw ParameterConversionException(
            parameterName = name,
            type = "UUID",
            cause = e
        )
    }
}

inline fun <reified T : Enum<T>> Parameters.getEnum(name: String): T? {
    return get(name)?.let { enumValueOf<T>(it) }
}

inline fun <reified T : Enum<T>> Parameters.getEnumOrFail(name: String): T {
    return getOrFail<T>(name)
}

fun String.tryToUuid(): Uuid = try {
    toUuid()
} catch (t: Throwable) {
    throw BadRequestException("Invalid id: $this")
}


fun Parameters.getLocalDate(name: String): LocalDate? {
    return get(name)?.let { value ->
        try {
            LocalDate.parse(value)
        } catch (e: DateTimeParseException) {
            throw ParameterConversionException(
                parameterName = name,
                type = "LocalDate",
                cause = e
            )
        }
    }
}

fun Parameters.getLocalDateTime(name: String): LocalDateTime? {
    return get(name)?.let { value ->
        try {
            LocalDateTime.parse(value)
        } catch (e: DateTimeParseException) {
            throw ParameterConversionException(
                parameterName = name,
                type = "LocalDateTime",
                cause = e
            )
        }
    }
}

fun Parameters.getLocalDateOrFail(name: String): LocalDate {
    val value = getOrFail(name)
    return try {
        LocalDate.parse(value)
    } catch (e: DateTimeParseException) {
        throw ParameterConversionException(
            parameterName = name,
            type = "LocalDate",
            cause = e
        )
    }
}

fun Parameters.getLocalDateTimeOrFail(name: String): LocalDateTime {
    val value = getOrFail(name)
    return try {
        LocalDateTime.parse(value)
    } catch (e: DateTimeParseException) {
        throw ParameterConversionException(
            parameterName = name,
            type = "LocalDateTime",
            cause = e
        )
    }
}