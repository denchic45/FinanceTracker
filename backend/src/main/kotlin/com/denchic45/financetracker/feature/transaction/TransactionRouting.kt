package com.denchic45.financetracker.feature.transaction

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.denchic45.financetracker.error.InvalidPageSize
import com.denchic45.financetracker.error.TransactionValidationMessages
import com.denchic45.financetracker.feature.buildValidationResult
import com.denchic45.financetracker.ktor.currentUserId
import com.denchic45.financetracker.transaction.model.AbstractTransactionRequest
import com.denchic45.financetracker.util.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.configureTransactions() {
    routing {
        authenticate("auth-jwt") {
            route("/transactions") {
                val repository by inject<TransactionRepository>()

                install(RequestValidation) {
                    validate<AbstractTransactionRequest> { request ->
                        buildValidationResult {
                            condition(
                                request.amount > 0, TransactionValidationMessages.AMOUNT_MUST_BE_POSITIVE
                            )
                        }
                    }
                }
                post {
                    repository.add(call.receive()).respond(HttpStatusCode.Created)
                }
                get {
                    either {
                        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                        val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 30

                        ensure(pageSize <= 30) { InvalidPageSize(30, pageSize) }
                        repository.find(currentUserId(), page, pageSize).bind()
                    }.respond()
                }
                route("/{transactionId}") {
                    get {
                        repository.findById(call.parameters.getOrFail<Long>("transactionId")).respond()
                    }
                    put {
                        repository.update(
                            call.parameters.getOrFail<Long>("transactionId"), call.receive()
                        ).respond()
                    }
                    delete {
                        repository.remove(call.parameters.getOrFail<Long>("transactionId"))
                            .respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}