package com.denchic45.financetracker.account.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    val id: Long,
    val name: String,
    val type: AccountType,
    val balance: Long
) {
    val displayedBalance = (balance / 100F).toString()
}