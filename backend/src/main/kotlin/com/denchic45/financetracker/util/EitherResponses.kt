package com.denchic45.financetracker.util

import arrow.core.Either
import com.denchic45.financetracker.error.DomainError
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext

context(routing: RoutingContext)
suspend inline fun <reified A : Any> Either<DomainError, A>.respond(
    status: HttpStatusCode = HttpStatusCode.OK
): Unit = when (this) {
    is Either.Left -> value.respond()
    is Either.Right -> routing.call.respond(status, this)
}

context(routing: RoutingContext)
suspend fun DomainError.respond(
) {
    routing.call.respond(httpCode, this)
}