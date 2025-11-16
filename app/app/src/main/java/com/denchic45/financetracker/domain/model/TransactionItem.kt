package com.denchic45.financetracker.domain.model

import com.denchic45.financetracker.ui.util.convertToCurrency
import kotlinx.datetime.LocalDateTime

sealed class TransactionItem {

    abstract val id: Long
    abstract val amount: Long
    abstract val datetime: LocalDateTime
    abstract val note: String
    abstract val account: AccountItem

    val displayedAmount: String
        get() = amount.convertToCurrency()


    data class Expense(
        override val id: Long,
        override val amount: Long,
        override val datetime: LocalDateTime,
        override val note: String,
        override val account: AccountItem,
        val category: CategoryItem,
        val tags: List<TagItem>
    ) : TransactionItem()

    data class Income(
        override val id: Long,
        override val amount: Long,
        override val datetime: LocalDateTime,
        override val note: String,
        override val account: AccountItem,
        val category: CategoryItem,
        val tags: List<TagItem>
    ) : TransactionItem()

    data class Transfer(
        override val id: Long,
        override val amount: Long,
        override val datetime: LocalDateTime,
        override val note: String,
        override val account: AccountItem,
        val incomeAccount: AccountItem
    ) : TransactionItem()
}