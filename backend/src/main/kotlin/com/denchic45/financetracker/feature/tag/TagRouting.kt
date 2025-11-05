package com.denchic45.financetracker.feature.tag

import com.denchic45.financetracker.api.error.TagValidationMessages
import com.denchic45.financetracker.feature.buildValidationResult
import com.denchic45.financetracker.api.tag.model.TagRequest
import com.denchic45.financetracker.util.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject


fun Application.configureTag() {
    routing {
        authenticate("auth-jwt") {
            route("/tags") {
                val repository by inject<TagRepository>()

                install(RequestValidation) {
                    validate<TagRequest> { request ->
                        buildValidationResult {
                            condition(
                                request.name.isNotBlank(),
                                TagValidationMessages.NAME_REQUIRED
                            )
                        }
                    }
                }
                post {
                    call.respond(HttpStatusCode.Created, repository.add(call.receive()))
                }
                route("/{tagId}") {
                    get {
                        repository.findById(call.parameters.getOrFail<Long>("tagId")).respond()
                    }
                    put {
                        repository.update(
                            call.parameters.getOrFail<Long>("tagId"),
                            call.receive()
                        ).respond()
                    }
                    delete {
                        repository.remove(call.parameters.getOrFail<Long>("tagId"))
                            .respond(HttpStatusCode.NoContent)
                    }
                }
                get {
                    call.respond(repository.findAll())
                }
            }
        }
    }
}