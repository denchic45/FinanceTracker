package com.denchic45.financetracker.data

import arrow.core.Ior
import arrow.core.left
import arrow.core.none
import arrow.core.some
import com.denchic45.financetracker.api.response.ApiResult
import com.denchic45.financetracker.api.response.EmptyApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


suspend inline fun <T> safeFetch(
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

fun <T> observeData(
    query: Flow<T>,
    fetch: suspend () -> ApiResult<*>,
    shouldFetch: (T) -> Boolean = { true },
    waitFetchResult: Boolean = true
): Flow<Ior<Failure, T>> = flow {
    val first = query.first()
    if (!waitFetchResult) emit(Ior.Right(first))
    if (shouldFetch(first)) {
        safeFetch { fetch() }.onLeft { failure ->
            emitAll(query.map { Ior.Both(failure, it) })
        }
    }
    emitAll(query.map { Ior.Right(it) })
}