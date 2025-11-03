package com.denchic45.financetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.denchic45.financetracker.account.model.AccountType
import com.denchic45.financetracker.transaction.model.TransactionType

data class AggregatedTransactionEntity(
    @ColumnInfo("transaction_id")
    val id: Long,
    val datetime: Long,
    val amount: Long,
    val type: TransactionType,
    val note: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "category_name")
    val categoryName: String,
    @ColumnInfo(name = "category_icon")
    val categoryIcon: String,
    @ColumnInfo(name = "category_income")
    val categoryIncome: Boolean,
    @Embedded(prefix = "account_")
    val account: TransactionAccountEntity,
    @Embedded(prefix = "income_account_")
    val incomeAccount: TransactionAccountEntity
)

data class TransactionAccountEntity(
    val id: Long,
    val name: String,
    val type: AccountType,
    val balance: Long
)