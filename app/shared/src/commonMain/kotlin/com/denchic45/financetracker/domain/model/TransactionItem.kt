package com.denchic45.financetracker.domain.model

import androidx.compose.runtime.Composable
import com.denchic45.financetracker.api.transaction.model.TransactionType
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.txn_expense
import financetracker_app.shared.generated.resources.txn_income
import financetracker_app.shared.generated.resources.txn_transfer
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource

sealed class TransactionItem {

    abstract val id: Long
    abstract val amount: Long
    abstract val datetime: LocalDateTime
    abstract val note: String
    abstract val account: AccountItem


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

@Composable
fun TransactionType.displayName() = stringResource(
    when (this) {
        TransactionType.EXPENSE -> Res.string.txn_expense
        TransactionType.INCOME -> Res.string.txn_income
        TransactionType.TRANSFER -> Res.string.txn_transfer
    }
)