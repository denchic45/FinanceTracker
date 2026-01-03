package com.denchic45.financetracker.data

import arrow.core.Ior
import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

inline fun <T> Ior<Failure, T?>.onRightHasValue(action: (T) -> Unit) = apply {
    when (this) {
        is Left<Failure> -> Unit
        is Both<Failure, T?> -> rightValue?.let { action(it) }
        is Right<T?> -> value?.let { action(it) }
    }
}

inline fun <T : Any?> Ior<Failure, T>.onRightHasNull(action: () -> Unit) = apply {
    when (this) {
        is Left<Failure> -> Unit
        is Both<Failure, T> -> if (rightValue == null) action()
        is Right<T> -> if (value == null) action()
    }
}

fun <T> Flow<Ior<Failure, T?>>.filterNotNullValue(): Flow<Ior<Failure, T>> {
    return transform<Ior<Failure, T?>, Ior<Failure, T>> { value ->
        if (value.getOrNull() != null) return@transform emit(value as Ior<Failure, T>)
    }
}