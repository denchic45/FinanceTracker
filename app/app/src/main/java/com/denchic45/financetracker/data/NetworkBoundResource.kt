package com.denchic45.financetracker.data

import arrow.core.Either
import arrow.core.Ior
import com.denchic45.financetracker.ui.Resource
import com.denchic45.financetracker.response.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


fun <T, R> observeData(
    query: Flow<T>,
    fetch: suspend () -> Either<Failure, R>,
    shouldFetch: (T) -> Boolean = { true },
): Flow<Ior<Failure, T>> = flow {
    val first = query.first()
    if (shouldFetch(first)) {
        fetch().onLeft { failure ->
            emitAll(query.map { Ior.Both(failure, it) })
        }
    }
    emitAll(query.map { Ior.Right(it) })
}