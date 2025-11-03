package com.denchic45.financetracker.feature.account

import com.denchic45.financetracker.account.model.AccountRequest
import com.denchic45.financetracker.error.AccountValidationMessages
import com.denchic45.financetracker.feature.buildValidationResult
import com.denchic45.financetracker.ktor.currentUserId
import com.denchic45.financetracker.ktor.getUuidOrFail
import com.denchic45.financetracker.util.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureAccounts() {
    routing {
        authenticate("auth-jwt") {
            route("/accounts") {
                install(RequestValidation) {
                    validate<AccountRequest> { request ->
                        buildValidationResult {
                            condition(request.name.isNotEmpty(), AccountValidationMessages.INVALID_NAME)
                        }
                    }
                }

                val repository by inject<AccountRepository>()

                post {
                    repository.add(call.receive(), currentUserId()).respond(HttpStatusCode.Created)
                }
                get {
                    repository.findAll(currentUserId()).respond()
                }
                route("/{accountId}") {
                    get {
                        repository.findById(
                            call.parameters.getUuidOrFail("accountId")
                        ).respond()
                    }
                    put {
                        repository.update(
                            call.parameters.getUuidOrFail("accountId"),
                            call.receive()
                        ).respond()
                    }
                    delete {
                        repository.remove(
                            call.parameters.getUuidOrFail("accountId")
                        ).respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}