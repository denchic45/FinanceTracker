package com.denchic45.financetracker.error

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
object EmailAlreadyUsed : ApiError {
    override val httpCode: HttpStatusCode get() = HttpStatusCode.Conflict
    override val message: String = "Email already used by another person."
}

@Serializable
object WrongPassword : BadRequestError() {
    override val message: String = "Wrong password."
}

@Serializable
object WrongEmail : BadRequestError() {
    override val message: String = "A user with this email address doesn't exist."
}

@Serializable
object InvalidGrantType : BadRequestError() {
    override val message: String = "The grant type provided does not exist."
}

@Serializable
object InvalidRefreshToken : BadRequestError() {
    override val message: String = "The refresh token is invalid or expired."
}


object SignUpValidationMessages {
    const val PASSWORD_TOO_SHORT = "Password must be at least 8 characters long."
    const val PASSWORD_MUST_CONTAIN_UPPERCASE = "Password must contain at least one uppercase letter (A-Z)."
    const val PASSWORD_MUST_CONTAIN_DIGITS = "Password must contain at least one digit (0-9)."
    const val PASSWORD_MUST_CONTAIN_SPECIAL_CHARACTERS = "Password must contain at least one special character (e.g., !@#\$%&*)."
    const val INVALID_EMAIL_FORMAT = "Invalid email format."
}