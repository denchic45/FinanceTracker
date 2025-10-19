package com.denchic45.financetracker.data

import com.denchic45.financetracker.domain.Resource
import com.denchic45.financetracker.domain.asFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


fun <T> observeResource(
    query: Flow<T>,
    fetch: suspend () -> Unit,
    shouldFetch: (T) -> Boolean = { true },
): Flow<Resource<T>> = flow {
    val first = query.first()
    if (shouldFetch(first)) {
        emit(Resource.Loading)
        val result = runCatching { fetch() }
        result
            .onFailure { throwable ->
                emit(
                    Resource.Failed(
                        throwable.asFailure(), first
                    )
                )
            }

        emitAll(query.map {
            result.exceptionOrNull()?.let { throwable ->
                Resource.Failed(throwable.asFailure(), it)
            } ?: Resource.Success(it)
        })
    } else emitAll(query.map { Resource.Success(it) })
}

suspend fun <T> fetchResource(fetch: suspend () -> T): Resource<T> {
    return runCatching { fetch() }
        .fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { throwable ->
                Resource.Failed(
                    throwable.asFailure(), null
                )
            }
        )
}

fun <T> fetchResourceFlow(
    fetch: suspend () -> T
): Flow<Resource<T>> = flow {
    emit(Resource.Loading)
    runCatching { fetch() }.onFailure { throwable ->
        emit(
            Resource.Failed(
                throwable.asFailure(), null
            )
        )
    }.onSuccess {
        emit(Resource.Success(it))
    }
}

