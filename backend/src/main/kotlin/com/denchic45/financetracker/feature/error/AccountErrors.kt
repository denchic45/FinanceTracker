package com.denchic45.financetracker.feature.error

import kotlinx.serialization.Serializable

@Serializable
object AccountNotFound : NotFoundError() {
    override val entity: String = "account"
}