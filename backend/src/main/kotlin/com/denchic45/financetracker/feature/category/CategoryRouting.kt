package com.denchic45.financetracker.feature.category

import com.denchic45.financetracker.api.category.model.CreateCategoryRequest
import com.denchic45.financetracker.api.error.CategoryValidationMessages
import com.denchic45.financetracker.feature.buildValidationResult
import com.denchic45.financetracker.ktor.currentUserId
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

fun Application.configureCategory() {
    routing {
        authenticate("auth-jwt") {
            route("/categories") {
                val repository by inject<CategoryRepository>()

                install(RequestValidation) {
                    validate<CreateCategoryRequest> { request ->
                        buildValidationResult {
                            condition(
                                request.name.isNotBlank(),
                                CategoryValidationMessages.NAME_REQUIRED
                            )
                            condition(
                                request.iconName.isNotBlank(),
                                CategoryValidationMessages.ICON_REQUIRED
                            )
                        }
                    }
                }
                get {
                    call.respond(
                        repository.findByType(
                            call.queryParameters.getOrFail<Boolean>("income"),
                            currentUserId()
                        )
                    )
                }
                post {
                    repository.add(call.receive(), currentUserId()).respond(HttpStatusCode.Created)
                }
                route("/{categoryId}") {
                    get {
                        repository.findById(call.parameters.getOrFail<Long>("categoryId")).respond()
                    }
                    put {
                        repository.update(
                            call.parameters.getOrFail<Long>("categoryId"),
                            call.receive()
                        ).respond()
                    }
                    delete {
                        repository.remove(call.parameters.getOrFail<Long>("categoryId"))
                            .respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}