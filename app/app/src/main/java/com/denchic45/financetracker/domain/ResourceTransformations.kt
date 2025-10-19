package com.denchic45.financetracker.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>
    data class Success<T>(val value: T) : Resource<T>
    data class Failed<T>(val failure: Failure, val value: T?) : Resource<T>
}


typealias EmptyResource = Resource<Unit>

fun loadingResource() = Resource.Loading

fun emptyResource(): EmptyResource = Resource.Success(Unit)

fun <T> resourceOf(value: T) = Resource.Success(value)

fun <T> resourceOf(failure: Failure, value: T?) = Resource.Failed(failure, value)

fun resourceOf() = loadingResource()


fun <T> Resource<T>.success() = this as Resource.Success


inline infix fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> = apply {
    if (this is Resource.Success) {
        action(value)
    }
}

inline infix fun <T> Resource<T>.onFailure(action: (Failure) -> Unit): Resource<T> = apply {
    if (this is Resource.Failed) {
        action(failure)
    }
}

inline infix fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> = apply {
    if (this is Resource.Loading) {
        action()
    }
}


inline fun <T, V> Resource<T>.map(transform: (T) -> V): Resource<V> {
    return when (this) {
        is Resource.Loading -> this
        is Resource.Success -> resourceOf(transform(value))
        is Resource.Failed -> Resource.Failed(failure, value?.let { transform(value) })
    }
}

fun <T> Flow<Resource<T>>.stateInResource(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.Lazily,
    initialValue: Resource<T> = Resource.Loading,
): StateFlow<Resource<T>> = stateIn(scope, started, initialValue)