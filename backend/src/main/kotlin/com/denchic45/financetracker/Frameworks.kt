package com.denchic45.financetracker

import com.denchic45.financetracker.feature.account.accountModule
import com.denchic45.financetracker.feature.auth.authModule
import com.denchic45.financetracker.feature.category.categoryModule
import com.denchic45.financetracker.feature.error.InvalidRequest
import com.denchic45.financetracker.feature.transaction.transactionModule
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(AutoHeadResponse)
    install(Koin) {
        slf4jLogger()
        modules(authModule, transactionModule, categoryModule, accountModule)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true // needed for optional properties
        })
    }

    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, InvalidRequest(cause.reasons))
        }
    }
}