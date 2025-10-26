package com.denchic45.financetracker.ui

import arrow.core.Either
import com.denchic45.financetracker.response.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * A sealed interface representing the current state of asynchronous data flow in the ViewModel.
 * This pattern allows the UI to react safely to data, loading status, or failures.
 */
sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>
    data class Success<T>(val value: T) : Resource<T>
    data class Failed(val failure: Failure) : Resource<Nothing>
}

// --- Constructors ---

fun <T> resourceOf(value: T) = Resource.Success(value)

fun resourceOf(failure: Failure) = Resource.Failed(failure)

fun resourceOf() = Resource.Loading


// --- Accessors (Use with caution, often better to use 'onSuccess' handlers) ---

/**
 * Casts the Resource to a [Resource.Success]. Should only be used when you are certain
 * the current state is Success (e.g., inside an onSuccess block or after a check).
 */
fun <T> Resource<T>.success() = this as Resource.Success


// --- Side-Effect Handlers ---

/**
 * Executes the [action] with the data value if the resource is [Resource.Success].
 */
inline infix fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> = apply {
    if (this is Resource.Success) {
        action(value)
    }
}

/**
 * Executes the [action] with the [Failure] if the resource is [Resource.Failed].
 */
inline infix fun <T> Resource<T>.onFailure(action: (Failure) -> Unit): Resource<T> = apply {
    if (this is Resource.Failed) {
        action(failure)
    }
}

/**
 * Executes the [action] if the resource is [Resource.Loading].
 */
inline infix fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> = apply {
    if (this is Resource.Loading) {
        action()
    }
}


// --- Transformation ---

/**
 * Transforms the data value of a [Resource.Success] using the [transform] function.
 * Propagates [Resource.Loading] and [Resource.Failed] states unchanged.
 *
 * @return A new Resource of type V.
 */
inline fun <T, V> Resource<T>.map(transform: (T) -> V): Resource<V> {
    return when (this) {
        is Resource.Loading -> this
        is Resource.Success -> resourceOf(transform(value))
        is Resource.Failed -> Resource.Failed(failure)
    }
}


// --- Flow Extensions ---

/**
 * Converts a [Flow] of [Either] (often from the Repository/UseCase layer) into a [StateFlow]
 * of [Resource]. This is the standard way to expose data to the ViewModel layer.
 *
 * - Left side of Either maps to [Resource.Failed].
 * - Right side of Either maps to [Resource.Success].
 *
 * @param scope CoroutineScope used to start the StateFlow (e.g., viewModelScope).
 * @param started Defines when the flow collection should start.
 * @param initialValue The starting state before the flow emits its first result.
 */
fun <T> Flow<Either<Failure, T>>.stateInResource(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.Lazily,
    initialValue: Resource<T> = Resource.Loading,
): StateFlow<Resource<T>> = map {
    it.fold(
        ifLeft = { resourceOf(it) },
        ifRight = { resourceOf(it) }
    )
}.stateIn(scope, started, initialValue)