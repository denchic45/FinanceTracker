package com.denchic45.financetracker.data.repository

import arrow.core.left
import arrow.core.none
import arrow.core.some
import com.denchic45.financetracker.data.ApiFailure
import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.asFailure
import com.denchic45.financetracker.api.response.ApiResult
import com.denchic45.financetracker.api.response.EmptyApiResult


suspend inline fun <reified T> safeFetch(
    request: suspend () -> ApiResult<T>,
): RequestResult<T> {
    return try {
        request().mapLeft { ApiFailure(it) }
    } catch (t: Throwable) {
        t.asFailure().left()
    }
}

suspend inline fun safeFetchForEmptyResult(
    request: suspend () -> EmptyApiResult,
): EmptyRequestResult {
    return try {
        request()
        none()
    } catch (t: Throwable) {
        return t.asFailure().some()
    }
}