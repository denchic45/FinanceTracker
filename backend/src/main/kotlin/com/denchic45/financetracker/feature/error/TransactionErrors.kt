package com.denchic45.financetracker.feature.error

import kotlinx.serialization.Serializable

@Serializable
object TransactionNotFound : NotFoundError() {
    override val entity: String = "transaction"
}