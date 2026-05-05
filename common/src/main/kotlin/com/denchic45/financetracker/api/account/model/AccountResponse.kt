package com.denchic45.financetracker.api.account.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AccountResponse(
    val id: Uuid,
    val name: String,
    val type: AccountType,
    val initialBalance: Long,
    val balance: Long,
    val iconName: String
) {
    val displayedBalance = (balance / 100F).toString()
}