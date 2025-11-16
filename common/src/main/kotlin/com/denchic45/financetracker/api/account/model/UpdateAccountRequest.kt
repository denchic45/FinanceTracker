package com.denchic45.financetracker.api.account.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccountRequest(
    val name: String,
    val type: AccountType,
    val adjustBalance: AdjustAccountBalanceRequest?
)