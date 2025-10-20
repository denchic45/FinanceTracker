package com.denchic45.financetracker.feature.category

import com.denchic45.financetracker.util.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.configureCategory() {
    routing {
        authenticate("auth-jwt") {
            route("/categories") {
                val repository: CategoryRepository by inject()

                post {
                    call.respond(HttpStatusCode.Created, repository.add(call.receive()))
                }
                route("/{categoryId}") {
                    get {
                        repository.findById(call.parameters.getOrFail<Long>("categoryId")).respond()
                    }
                    put {
                        call.respond(
                            repository.update(
                                call.parameters.getOrFail<Long>("categoryId"),
                                call.receive()
                            )
                        )
                    }
                    delete {
                        repository.remove(call.parameters.getOrFail<Long>("categoryId"))
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}