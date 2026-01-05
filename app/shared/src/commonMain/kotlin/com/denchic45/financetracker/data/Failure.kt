package com.denchic45.financetracker.data

import com.denchic45.financetracker.api.error.ApiError
import com.denchic45.financetracker.api.response.UnknownApiException
import com.denchic45.financetracker.api.response.bodyNullable
import com.denchic45.financetracker.api.response.isNetworkException
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.common_error_api_forbidden
import financetracker_app.shared.generated.resources.common_error_api_not_found
import financetracker_app.shared.generated.resources.common_error_api_server
import financetracker_app.shared.generated.resources.common_error_api_unhandled
import financetracker_app.shared.generated.resources.common_error_internal
import financetracker_app.shared.generated.resources.common_error_no_connection
import financetracker_app.shared.generated.resources.common_error_unknown
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import org.jetbrains.compose.resources.StringResource


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
 * was successfully parsed into a structured [ApiFailure] object.
 *
 * @property error The structured domain error object.
 */
data class ApiFailure(
    val error: ApiError
) : Failure

/**
 * Represents an API failure where the HTTP status code (4xx/5xx) indicates an issue,
 * but the response body could not be parsed into a structured [ApiError].
 *
 * @property code The HTTP status code (e.g., 404, 500).
 * @property body The raw response body text.
 */
data class UnknownApiFailure(
    val code: Int,
    val body: String
) : Failure

/**
 * Represents an unexpected, unhandled runtime exception that occurred outside of
 * typical network or API error flows.
 *
 * @property throwable The original [Throwable].
 */
data class ThrowableFailure(
    val throwable: Throwable
) : Failure

// --- Exception Wrappers ---

/**
 * A specific exception thrown to wrap a structured [ApiError] for propagation
 * up the call stack.
 *
 * @property apiError The structured domain error.
 */
class ApiException(val apiError: ApiError) : Exception(apiError.message)

// --- Extension Functions ---

/**
 * Converts a generic [Throwable] caught during a suspend call into a domain-level [Failure].
 *
 * This function handles mapping common infrastructure exceptions:
 * - [UnknownApiException] is mapped to [UnknownApiFailure].
 * - [IOException] or platform-specific network exceptions are mapped to [NoConnection].
 * - All other exceptions are mapped to [ThrowableFailure].
 *
 * @receiver The caught [Throwable].
 * @return The corresponding [Failure] object.
 */
fun Throwable.asFailure(): Failure = when (this) {
    is UnknownApiException -> UnknownApiFailure(
        this.code,
        this.message.orEmpty()
    )

    else -> {
        printStackTrace()
        if (isNetworkException()) NoConnection
        else ThrowableFailure(this)
    }
}

/**
 * Attempts to convert an erroneous [HttpResponse] (e.g., 4xx or 5xx status) into a [Failure].
 *
 * This function first attempts to deserialize the response body into a structured [ApiError].
 * If successful, it returns [ApiError]. Otherwise, it returns [UnknownApiFailure] with the
 * status code and raw body text.
 *
 * @receiver The Ktor [HttpResponse] object.
 * @return An [ApiError] if the body is a [ApiError], otherwise [UnknownApiFailure].
 */
suspend fun HttpResponse.asFailure(): Failure {
    return bodyNullable<ApiError>()
        ?.let { ApiFailure(it) }
        ?: UnknownApiFailure(
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
        is ApiFailure -> ApiException(error)
        is UnknownApiFailure -> UnknownApiException(code, body)
        is ThrowableFailure -> throwable
    }
}

fun Failure.getDefaultErrorMessageResource(): StringResource = when (this) {
    NoConnection -> Res.string.common_error_no_connection
    is ApiFailure -> getDefaultApiErrorMessageResource()
    is UnknownApiFailure -> Res.string.common_error_unknown
    is ThrowableFailure -> Res.string.common_error_internal
}

fun ApiFailure.getDefaultApiErrorMessageResource(): StringResource = when (error.httpCode) {
    HttpStatusCode.Forbidden -> Res.string.common_error_api_forbidden
    HttpStatusCode.NotFound -> Res.string.common_error_api_not_found
    in HttpStatusCode.InternalServerError..HttpStatusCode.InternalServerError -> Res.string.common_error_api_server
    else -> Res.string.common_error_api_unhandled
}