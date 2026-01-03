package com.denchic45.financetracker.data

import arrow.core.Either
import arrow.core.Option
import arrow.core.none
import arrow.core.some

typealias RequestResult<T> = Either<Failure, T>

typealias EmptyRequestResult = Option<Failure>

fun <T> RequestResult<T>.toEmptyRequestResult(): Option<Failure> = fold(
    ifLeft = { it.some() },
    ifRight = { none() }
)