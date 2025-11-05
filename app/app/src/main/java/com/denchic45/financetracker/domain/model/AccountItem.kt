package com.denchic45.financetracker.domain.model

import com.denchic45.financetracker.api.account.model.AccountType
import java.util.UUID

data class AccountItem(
    val id: UUID,
    val name: String,
    val type: AccountType,
    val balance: Long
) {
    val displayedBalance = (balance / 100F).toString()
}