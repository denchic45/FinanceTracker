package com.denchic45.financetracker.api.account.model

import com.denchic45.financetracker.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AccountResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val type: AccountType,
    val initialBalance: Long,
    val balance: Long
) {
    val displayedBalance = (balance / 100F).toString()
}