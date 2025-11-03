package com.denchic45.financetracker

import arrow.core.right
import com.denchic45.financetracker.response.ApiResult
import com.denchic45.financetracker.response.EmptyApiResult
import com.denchic45.financetracker.util.leftValue
import com.denchic45.financetracker.util.rightValue
import com.denchic45.financetracker.util.rightValueOrNull
import com.denchic45.financetracker.util.someValue
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

fun assertResultIsOk(result: ApiResult<*>) {
    assertNotNull(result.rightValueOrNull) {
        val error = result.leftValue
        "status: ${error.httpCode.value} reason: ${error.message}"
    }
}

fun assertResultIsError(result: ApiResult<*>) {
    assertNotNull(result.leftValue) { result.right().toString() }
}

fun <T> ApiResult<T>.assertedRight() = apply(::assertResultIsOk).rightValue

fun <T> ApiResult<T>.assertedLeft() = apply(::assertResultIsError).leftValue


fun assertResultIsNone(result: EmptyApiResult) {
    assertTrue(result.isNone()) {
        val error = result.someValue
        "status: ${error.httpCode.value} reason: ${error.message}"
    }
}

fun assertResultIsSome(result: EmptyApiResult) {
    assertTrue(result.isSome()) { "result is None" }
}

fun EmptyApiResult.assertedNone() = apply(::assertResultIsNone)

fun EmptyApiResult.assertedSome() = apply(::assertResultIsSome).someValue