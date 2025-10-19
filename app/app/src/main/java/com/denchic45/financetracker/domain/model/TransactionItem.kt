package com.denchic45.financetracker.domain.model

import com.denchic45.financetracker.transaction.model.TransactionType
import java.text.NumberFormat
import java.util.Locale

data class TransactionItem(
    val id: Long,
    val amount: Long,
    val type: TransactionType,
    val description: String,
    val account: AccountItem,
    val category: CategoryItem,
    val incomeAccount: AccountItem
) {

    val formattedAmount: String
        get() {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            return currencyFormat.format(amount / 100.0)
        }
}