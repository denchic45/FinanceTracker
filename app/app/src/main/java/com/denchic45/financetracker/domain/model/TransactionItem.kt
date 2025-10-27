package com.denchic45.financetracker.domain.model

import java.text.NumberFormat
import java.util.Locale

sealed class TransactionItem {

    abstract val id: Long
    abstract val amount: Long
    abstract val note: String
    abstract val account: AccountItem

    val formattedAmount: String
        get() {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            return currencyFormat.format(amount / 100.0)
        }

    data class Expense(
        override val id: Long,
        override val amount: Long,
        override val note: String,
        override val account: AccountItem,
        val category: CategoryItem
    ) : TransactionItem()

    data class Income(
        override val id: Long,
        override val amount: Long,
        override val note: String,
        override val account: AccountItem,
        val category: CategoryItem
    ) : TransactionItem()

    data class Transfer(
        override val id: Long,
        override val amount: Long,
        override val note: String,
        override val account: AccountItem,
        val incomeAccount: AccountItem
    ) : TransactionItem()
}