package com.denchic45.financetracker.ktor

import com.auth0.jwt.interfaces.Payload
import com.denchic45.financetracker.util.toUuid
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlin.uuid.Uuid

val Payload.claimSub: Uuid
    get() = getClaim("sub").asString().toUuid()

fun RoutingContext.currentUserId(): Uuid {
    return call.jwtPrincipal().payload.claimSub
}

fun RoutingContext.getUserUuidByParameterOrMe(name: String): Uuid {
    return when (val value = call.parameters.getOrFail(name)) {
        "me" -> currentUserId()
        else -> value.tryToUuid()
    }
}

fun RoutingContext.getUserUuidByQueryParameterOrMe(name: String): Uuid? {
    return when (val value = call.request.queryParameters[name]) {
        "me" -> currentUserId()
        else -> value?.tryToUuid()
    }
}

fun RoutingContext.requireUserUuidByQueryParameterOrMe(name: String): Uuid {
    return getUserUuidByQueryParameterOrMe(name) ?: throw MissingRequestParameterException(name)
}