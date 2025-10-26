package com.denchic45.financetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.PrimaryKey
import com.denchic45.financetracker.transaction.model.TransactionType
import java.util.UUID

@Entity(
    tableName = "transaction",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["account_id"],
            childColumns = ["account_id"],
            onDelete = NO_ACTION,
            onUpdate = NO_ACTION
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = NO_ACTION,
            onUpdate = NO_ACTION
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["account_id"],
            childColumns = ["income_account_id"],
            onDelete = NO_ACTION,
            onUpdate = NO_ACTION
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey()
    @ColumnInfo("transaction_id")
    val id: Long,
    val datetime: Long,
    val amount: Long,
    val type: TransactionType,
    val description: String,
    @ColumnInfo("account_id")
    val accountId: UUID,
    @ColumnInfo("category_id")
    val categoryId: Long,
    @ColumnInfo("income_account_id")
    val incomeAccountId: UUID?,
)