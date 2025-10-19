package com.denchic45.financetracker.account.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountRequest(
    val name: String,
    val type: AccountType
)