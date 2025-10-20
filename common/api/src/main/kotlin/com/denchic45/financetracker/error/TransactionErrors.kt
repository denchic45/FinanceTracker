package com.denchic45.financetracker.error

import kotlinx.serialization.Serializable

@Serializable
object TransactionNotFound : NotFoundError() {
    override val entity: String = "transaction"
}

object TransactionValidationMessages {
    const val INCOME_ACCOUNT_REQUIRED_ON_TRANSFER = "The income account is required on transfer"
    const val INCOME_ACCOUNT_MUST_BE_NULL = "The income account must be null"
}