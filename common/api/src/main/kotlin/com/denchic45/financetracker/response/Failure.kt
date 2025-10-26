package com.denchic45.financetracker.response

import com.denchic45.financetracker.bodyNullable
import com.denchic45.financetracker.error.DomainError
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.io.IOException


/**
 * Sealed interface representing all possible failure states originating from the data layer
 * that need to be communicated up to the domain or presentation layers.
 */
sealed interface Failure

/**
 * Represents a failure due to lack of network connectivity (e.g., airplane mode, no Wi-Fi).
 * Typically mapped from [IOException] or specific platform network exceptions.
 */
data object NoConnection : Failure

/**
 * Represents a business logic error returned from the API, where the response body
 * was successfully parsed into a structured [DomainError] object.
 *
 * @property error The structured domain error object.
 */
data class ApiError(
    val error: DomainError
) : Failure

/**
 * Represents an API failure where the HTTP status code (4xx/5xx) indicates an issue,
 * but the response body could not be parsed into a structured [DomainError].
 *
 * @property code The HTTP status code (e.g., 404, 500).
 * @property body The raw response body text.
 */
data class UnknownApiError(
    val code: Int,
    val body: String
) : Failure

/**
 * Represents an unexpected, unhandled runtime exception that occurred outside of
 * typical network or API error flows.
 *
 * @property throwable The original [Throwable].
 */
data class ThrowableError(
    val throwable: Throwable
) : Failure

// --- Exception Wrappers ---

/**
 * A generic exception thrown to wrap an [UnknownApiError] for compatibility with
 * standard Kotlin error handling mechanisms (try-catch).
 *
 * @property code The HTTP status code.
 * @param message The raw response body or generic error message.
 */
class ApiException(val code: Int, message: String) : Exception(message)

/**
 * A specific exception thrown to wrap a structured [DomainError] for propagation
 * up the call stack.
 *
 * @property domainError The structured domain error.
 */
class DomainApiException(val domainError: DomainError) : Exception(domainError.message)

// --- Extension Functions ---

/**
 * Converts a generic [Throwable] caught during a suspend call into a domain-level [Failure].
 *
 * This function handles mapping common infrastructure exceptions:
 * - [ApiException] is mapped to [UnknownApiError].
 * - [IOException] or platform-specific network exceptions are mapped to [NoConnection].
 * - All other exceptions are mapped to [ThrowableError].
 *
 * @receiver The caught [Throwable].
 * @return The corresponding [Failure] object.
 */
fun Throwable.asFailure(): Failure = when (this) {
    is ApiException -> UnknownApiError(
        this.code,
        this.message.orEmpty()
    )

    is IOException -> NoConnection
    else -> {
        if (isNetworkException()) NoConnection
        else ThrowableError(this)
    }
}

/**
 * Attempts to convert an erroneous [HttpResponse] (e.g., 4xx or 5xx status) into a [Failure].
 *
 * This function first attempts to deserialize the response body into a structured [DomainError].
 * If successful, it returns [ApiError]. Otherwise, it returns [UnknownApiError] with the
 * status code and raw body text.
 *
 * @receiver The Ktor [HttpResponse] object.
 * @return An [ApiError] if the body is a [DomainError], otherwise [UnknownApiError].
 */
suspend fun HttpResponse.asFailure(): Failure {
    return bodyNullable<DomainError>()
        ?.let { ApiError(it) }
        ?: UnknownApiError(
            status.value,
            bodyAsText()
        )
}

/**
 * Converts a domain-level [Failure] back into a [Throwable] exception.
 *
 * This is primarily used when propagating an error that was previously handled
 * (e.g., in a delegated refresh task) or for testing purposes.
 *
 * @receiver The [Failure] object.
 * @return The corresponding [Throwable] to be thrown or handled.
 */
fun Failure.asThrowable(): Throwable {
    return when (this) {
        NoConnection -> IOException()
        is ApiError -> DomainApiException(error)
        is UnknownApiError -> ApiException(code, body)
        is ThrowableError -> throwable
    }
}