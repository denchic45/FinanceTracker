package com.denchic45.financetracker.feature.transaction

import com.denchic45.financetracker.transaction.model.TransactionErrors
import com.denchic45.financetracker.transaction.model.TransactionRequest
import com.denchic45.financetracker.transaction.model.TransactionType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.configureTransactions() {
    routing {
        authenticate("auth-jwt") {
            route("/transactions") {
                val repository: TransactionRepository by inject()

                install(RequestValidation) {
                    validate<TransactionRequest> { request ->
                        when {
                            request.type == TransactionType.TRANSFER && request.incomeSourceId == null -> {
                                ValidationResult.Invalid(TransactionErrors.INCOME_ACCOUNT_REQUIRED_ON_TRANSFER)
                            }

                            request.type != TransactionType.TRANSFER && request.incomeSourceId != null -> {
                                ValidationResult.Invalid(TransactionErrors.INCOME_ACCOUNT_MUST_BE_NULL)
                            }

                            else -> ValidationResult.Valid
                        }
                    }
                }
                post {
                    call.respond(HttpStatusCode.Created, repository.add(call.receive()))
                }
                get {
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 30

                    call.respond(repository.find(page, pageSize))
                }
                route("/{transactionId}") {
                    get {
                        call.respond(repository.findById(call.parameters.getOrFail<Long>("transactionId")))
                    }
                    put {
                        call.respond(
                            repository.update(
                                call.parameters.getOrFail<Long>("transactionId"),
                                call.receive()
                            )
                        )
                    }
                    delete {
                        repository.remove(call.parameters.getOrFail<Long>("transactionId"))
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}