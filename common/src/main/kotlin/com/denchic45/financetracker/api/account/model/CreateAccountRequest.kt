package com.denchic45.financetracker.api.account.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    val name: String,
    val type: AccountType,
    val initialBalance: Long,
    val iconName: String,
)