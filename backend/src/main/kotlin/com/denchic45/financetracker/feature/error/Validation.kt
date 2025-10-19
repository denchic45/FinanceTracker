package com.denchic45.financetracker.feature.error

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable

class ValidationResultBuilder {
    private val errors: MutableList<String> = mutableListOf()
    fun condition(condition: Boolean, errorMessage: String) {
        if (!condition) errors.add(errorMessage)
    }

    fun build(): ValidationResult {
        return if (errors.isEmpty())
            ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }

}

fun buildValidationResult(block: ValidationResultBuilder.() -> Unit): ValidationResult {
    val builder = ValidationResultBuilder().apply(block)
    return builder.build()
}

@Serializable
sealed class FailedValidation() : DomainError {
    override val httpCode: HttpStatusCode get() = HttpStatusCode.BadRequest
}

@Serializable
data class InvalidRequest(val errors: List<String>) : FailedValidation() {
    override val message: String = "Request has invalid fields"
}