package com.denchic45.financetracker.data

import arrow.core.Ior
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

inline fun <T> Ior<Failure, T>.ifHasRightValue(action: (T) -> Unit) = apply {
    when (this) {
        is Ior.Left<Failure> -> Unit
        is Ior.Both<Failure, T> -> action(rightValue)
        is Ior.Right<T> -> action(value)
    }
}

fun <T> Flow<Ior<Failure, T?>>.filterNotNullValue(): Flow<Ior<Failure, T>> {
    return transform<Ior<Failure, T?>, Ior<Failure, T>> { value ->
        if (value.getOrNull() != null) return@transform emit(value as Ior<Failure, T>)
    }
}
