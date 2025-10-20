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