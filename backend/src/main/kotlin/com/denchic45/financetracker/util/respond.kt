package com.denchic45.financetracker.util

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.denchic45.financetracker.error.ApiError
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext

context(routing: RoutingContext)
suspend inline fun <reified A : Any> Either<ApiError, A>.respond(
    status: HttpStatusCode = HttpStatusCode.OK
): Unit = when (this) {
    is Either.Left -> value.respond()
    is Either.Right -> {
        routing.call.respond(status, value)
    }
}

context(routing: RoutingContext)
suspend inline fun Option<ApiError>.respond(
    status: HttpStatusCode = HttpStatusCode.OK
): Unit = when (this) {
    None -> routing.call.respond(status)
    is Some -> value.respond()
}

context(routing: RoutingContext)
suspend fun ApiError.respond(
) {
    routing.call.respond(httpCode, this)
}