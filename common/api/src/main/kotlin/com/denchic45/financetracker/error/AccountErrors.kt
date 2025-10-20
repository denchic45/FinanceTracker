package com.denchic45.financetracker.error

import kotlinx.serialization.Serializable

@Serializable
object AccountNotFound : NotFoundError() {
    override val entity: String = "account"
}

object AccountValidationMessages {
    const val INVALID_NAME = "Invalid account name."
}