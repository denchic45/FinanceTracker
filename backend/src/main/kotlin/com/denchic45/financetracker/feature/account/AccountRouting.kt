package com.denchic45.financetracker.feature.account

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.configureAccounts() {
    routing {
        authenticate("auth-jwt") {
            route("/accounts") {
                val repository: AccountRepository by inject()
                post {
                    call.respond(HttpStatusCode.Created, repository.add(call.receive()))
                }
                get {
                    call.respond(repository.findAll())
                }
                route("/{accountId}") {
                    get {
                        call.respond(repository.findById(call.parameters.getOrFail<Long>("accountId")))
                    }
                    put {
                        call.respond(
                            repository.update(
                                call.parameters.getOrFail<Long>("accountId"),
                                call.receive()
                            )
                        )
                    }
                    delete {
                        repository.remove(call.parameters.getOrFail<Long>("accountId"))
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}