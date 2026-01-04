package com.denchic45.financetracker.feature.account

import arrow.core.raise.either
import com.denchic45.financetracker.api.account.model.CreateAccountRequest
import com.denchic45.financetracker.api.account.model.UpdateAccountRequest
import com.denchic45.financetracker.api.error.AccountValidationMessages
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
                    validate<CreateAccountRequest> { request ->
                        buildValidationResult {
                            condition(request.name.isNotEmpty(), AccountValidationMessages.INVALID_NAME)
                        }
                    }
                    validate<UpdateAccountRequest> { request ->
                        buildValidationResult {
                            condition(request.name.isNotEmpty(), AccountValidationMessages.INVALID_NAME)
                        }
                    }
                }

                val accountRepository by inject<AccountRepository>()

                post {
                    accountRepository.add(call.receive(), currentUserId()).respond(HttpStatusCode.Created)
                }
                get {
                    call.respond(accountRepository.findAll(currentUserId()))
                }
                route("/{accountId}") {
                    get {
                        accountRepository.findById(
                            call.parameters.getUuidOrFail("accountId")
                        ).respond()
                    }
                    put {
                        either {
                            val request = call.receive<UpdateAccountRequest>()
                            val account = accountRepository.update(
                                call.parameters.getUuidOrFail("accountId"),
                                request
                            ).bind()
                            request.adjustBalance?.let { adjustBalance ->
                                TODO("Later...")
//                                val balance = account.balance
//                                if (adjustBalance.createTransaction) {
//                                    transactionRepository.addAdjustBalanceTransaction()
//                                }
                            } ?: account
                        }.respond()
                    }
                    delete {
                        accountRepository.remove(
                            call.parameters.getUuidOrFail("accountId")
                        ).respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}