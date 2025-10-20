package com.denchic45.financetracker.ktor

import com.auth0.jwt.interfaces.Payload
import com.denchic45.financetracker.util.toUUID
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.util.*

val Payload.claimSub: UUID
    get() = getClaim("sub").asString().toUUID()

fun RoutingContext.currentUserId(): UUID {
    return call.jwtPrincipal().payload.claimSub
}

fun RoutingContext.getUserUuidByParameterOrMe(name: String): UUID {
    return when (val value = call.parameters.getOrFail(name)) {
        "me" -> currentUserId()
        else -> value.tryToUUID()
    }
}

fun RoutingContext.getUserUuidByQueryParameterOrMe(name: String): UUID? {
    return when (val value = call.request.queryParameters[name]) {
        "me" -> currentUserId()
        else -> value?.tryToUUID()
    }
}

fun RoutingContext.requireUserUuidByQueryParameterOrMe(name: String): UUID {
    return getUserUuidByQueryParameterOrMe(name) ?: throw MissingRequestParameterException(name)
}