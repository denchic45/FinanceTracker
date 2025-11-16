package com.denchic45.financetracker.api.account.model

import kotlinx.serialization.Serializable

@Serializable
data class AdjustAccountBalanceRequest(
    val balance: Long,
    val createTransaction: Boolean
)