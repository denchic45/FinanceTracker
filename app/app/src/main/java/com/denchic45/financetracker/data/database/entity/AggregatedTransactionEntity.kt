package com.denchic45.financetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.api.transaction.model.TransactionType
import java.util.UUID

data class AggregatedTransactionEntity(
    @ColumnInfo("transaction_id")
    val id: Long,
    val datetime: Long,
    val amount: Long,
    val type: TransactionType,
    val note: String,
    @Embedded()
    val category: CategoryEntity?,
    @Embedded(prefix = "account_")
    val account: TransactionAccountEntity,
    @Embedded(prefix = "income_account_")
    val incomeAccount: TransactionAccountEntity?,
    @Relation(
        parentColumn = "transaction_id",
        entityColumn = "tag_id",
        associateBy = Junction(TransactionTagCrossRef::class)
    )
    val tags: List<TagEntity>
)

data class TransactionAccountEntity(
    val id: UUID,
    val name: String,
    val type: AccountType,
    val balance: Long
)