package com.denchic45.financetracker.feature.auth

import arrow.core.raise.either
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.denchic45.financetracker.auth.model.SignUpRequest
import com.denchic45.financetracker.error.SignUpValidationMessages
import com.denchic45.financetracker.feature.buildValidationResult
import com.denchic45.financetracker.util.respond
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import java.util.regex.Pattern


fun Route.authRoute() {
    val authService: AuthService by inject()
    val config = environment.config

    fun generateToken(userId: UUID): String {
        return JWT.create()
            .withAudience(config.property("jwt.audience").getString())
            .withClaim("id", userId.toString())
            .withExpiresAt(LocalDateTime.now().plusWeeks(1).atZone(ZoneId.systemDefault()).toInstant())
            .sign(Algorithm.HMAC256(config.property("jwt.secret").getString()))
    }

    install(RequestValidation) {
        validate<SignUpRequest> { request ->
            val password = request.password
            buildValidationResult {
                condition(password.length >= 8, SignUpValidationMessages.PASSWORD_TOO_SHORT)
                condition(
                    Pattern.compile(".*[A-Z].*").matcher(password).matches(),
                    SignUpValidationMessages.PASSWORD_MUST_CONTAIN_UPPERCASE
                )
                condition(
                    Pattern.compile(".*[0-9].*").matcher(password).matches(),
                    SignUpValidationMessages.PASSWORD_MUST_CONTAIN_DIGITS
                )
                condition(
                    Pattern.compile(".*[!@#$%^&*()\\-+=_`~,.<>/?|].*").matcher(password).matches(),
                    SignUpValidationMessages.PASSWORD_MUST_CONTAIN_SPECIAL_CHARACTERS
                )
                condition(
                    request.email.isNotBlank() && request.email.contains("@"),
                    SignUpValidationMessages.INVALID_EMAIL_FORMAT
                )
            }
        }
    }

    post("/token") {
        either {
            val userId = authService.findUserId(call.receive()).bind()
            generateToken(userId)
        }.respond()
    }

    post("/sign-up") {
        either {
            val userId = authService.signUp(call.receive()).bind()
            generateToken(userId)
        }.respond()
    }
}