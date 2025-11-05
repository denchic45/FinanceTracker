package com.denchic45.financetracker.api.error

import kotlinx.serialization.Serializable

@Serializable
object TransactionNotFound : NotFoundError() {
    override val entity: String = "transaction"
}

object TransactionValidationMessages {
    const val AMOUNT_MUST_BE_POSITIVE = "Amount must be greater than zero."
}