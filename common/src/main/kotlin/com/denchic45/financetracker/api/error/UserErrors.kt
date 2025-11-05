package com.denchic45.financetracker.api.error

import kotlinx.serialization.Serializable

@Serializable
object UserNotFound : NotFoundError() {
    override val entity: String = "user"
}