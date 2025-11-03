package com.denchic45.financetracker.ui

import arrow.core.Ior
import com.denchic45.financetracker.data.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface CacheableResource<out A> {
    object Loading : CacheableResource<Nothing>
    data class Newest<A>(val value: A) : CacheableResource<A>
    data class Failed(val error: Failure) : CacheableResource<Nothing>
    data class Cached<A>(val error: Failure, val value: A) : CacheableResource<A>
}

// --- Constructors ---

fun <T> cacheableResourceOf(value: T) = CacheableResource.Newest(value)

fun cacheableResourceOf(failure: Failure) = CacheableResource.Failed(failure)

fun cacheableResourceOf() = CacheableResource.Loading

// --- Accessors ---

/**
 * Convenience cast to access the Newest data property, throws if not Newest.
 */
fun <T> CacheableResource<T>.newest() = this as CacheableResource.Newest

/**
 * Returns the cached or newest value, or null if loading or failed without cache.
 */
fun <T> CacheableResource<T>.valueOrNull(): T? = when (this) {
    is CacheableResource.Newest -> value
    is CacheableResource.Cached -> value
    else -> null
}

/**
 * Returns the error if failed or cached, or null otherwise.
 */
fun <T> CacheableResource<T>.failureOrNull(): Failure? = when (this) {
    is CacheableResource.Failed -> error
    is CacheableResource.Cached -> error
    else -> null
}

// --- Side-Effect Handlers ---

inline infix fun <T> CacheableResource<T>.onNewest(action: (T) -> Unit): CacheableResource<T> = apply {
    if (this is CacheableResource.Newest) {
        action(value)
    }
}

inline infix fun <T> CacheableResource<T>.onFailed(action: (Failure) -> Unit): CacheableResource<T> = apply {
    if (this is CacheableResource.Failed) {
        action(error)
    }
}

inline infix fun <T> CacheableResource<T>.onLoading(action: () -> Unit): CacheableResource<T> = apply {
    if (this is CacheableResource.Loading) {
        action()
    }
}

/**
 * Executes action if data is present, regardless of whether it's new or cached with an error.
 */
inline infix fun <T> CacheableResource<T>.onData(action: (T) -> Unit): CacheableResource<T> = apply {
    when (this) {
        is CacheableResource.Newest -> action(value)
        is CacheableResource.Cached -> action(value)
        else -> Unit
    }
}

/**
 * Executes action only if data is cached (i.e., data is present but fetch failed).
 */
inline infix fun <T> CacheableResource<T>.onCached(action: (Failure, T) -> Unit): CacheableResource<T> = apply {
    if (this is CacheableResource.Cached) {
        action(error, value)
    }
}

// --- Transformation ---

inline fun <T, V> CacheableResource<T>.map(transform: (T) -> V): CacheableResource<V> {
    return when (this) {
        is CacheableResource.Loading -> this
        is CacheableResource.Newest -> cacheableResourceOf(transform(value))
        is CacheableResource.Failed -> CacheableResource.Failed(error)
        is CacheableResource.Cached -> CacheableResource.Cached(error, transform(value))
    }
}


// --- Flow Extensions ---

/**
 * Converts a [Flow] of [Ior] (Inclusive-or, often used for data-caching patterns)
 * into a [StateFlow] of [CacheableResource].
 *
 * - Ior.Left(Error) maps to [CacheableResource.Failed].
 * - Ior.Right(Value) maps to [CacheableResource.Newest].
 * - Ior.Both(Error, Value) maps to [CacheableResource.Cached].
 *
 * @param scope CoroutineScope used to start the StateFlow (e.g., viewModelScope).
 * @param started Defines when the flow collection should start.
 * @param initialValue The starting state, typically [CacheableResource.Loading].
 */
fun <T> Flow<Ior<Failure, T>>.stateInCacheableResource(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.Lazily,
    initialValue: CacheableResource<T> = CacheableResource.Loading,
): StateFlow<CacheableResource<T>> = map {
    it.fold(
        fa = { CacheableResource.Failed(it) },
        fb = { CacheableResource.Newest(it) },
        fab = { error, value -> CacheableResource.Cached(error, value) }
    )
}.stateIn(scope, started, initialValue)
