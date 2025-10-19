package com.denchic45.financetracker.feature.error

import arrow.core.Either
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface DomainError {
    val httpCode: HttpStatusCode
    val message: String
}

@Serializable
sealed class BadRequestError : DomainError {
    override val httpCode: HttpStatusCode get() = HttpStatusCode.BadRequest
}

@Serializable
sealed class NotFoundError() : DomainError {
    override val httpCode: HttpStatusCode get() = HttpStatusCode.NotFound
    override val message: String = "Entity not found"
    abstract val entity: String
}

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