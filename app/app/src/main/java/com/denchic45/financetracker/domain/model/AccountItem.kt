package com.denchic45.financetracker.domain.model

import com.denchic45.financetracker.account.model.AccountType

data class AccountItem(
    val id: Long,
    val name: String,
    val type: AccountType,
    val balance: Long
) {
    val displayedBalance = (balance / 100F).toString()
}