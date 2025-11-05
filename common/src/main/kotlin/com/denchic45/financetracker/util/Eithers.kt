package com.denchic45.financetracker.util

import arrow.core.Either


val <A, B> Either<A, B>.leftValue
    get() = (this as Either.Left).value

val <A, B> Either<A, B>.rightValue
    get() = (this as Either.Right).value

val <A, B> Either<A, B>.rightValueOrNull
    get() = (this as? Either.Right)?.value