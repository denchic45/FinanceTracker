package com.denchic45.financetracker.api.error

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface ApiError {
    val httpCode: HttpStatusCode
    val message: String
}

@Serializable
sealed class BadRequestError : ApiError {
    override val httpCode: HttpStatusCode get() = HttpStatusCode.BadRequest
}

@Serializable
sealed class NotFoundError() : ApiError {
    override val httpCode: HttpStatusCode get() = HttpStatusCode.NotFound
    override val message: String = "Entity not found"
    abstract val entity: String
}

@Serializable
sealed class FailedValidation() : ApiError {
    override val httpCode: HttpStatusCode get() = HttpStatusCode.BadRequest
}

@Serializable
data class InvalidRequest(val errors: List<String>) : FailedValidation() {
    override val message: String = "Request has invalid fields"
}