package com.denchic45.financetracker.error

import kotlinx.serialization.Serializable

@Serializable
object UserNotFound : NotFoundError() {
    override val entity: String = "user"
}