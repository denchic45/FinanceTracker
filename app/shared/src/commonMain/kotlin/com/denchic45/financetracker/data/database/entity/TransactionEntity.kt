package com.denchic45.financetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.denchic45.financetracker.api.transaction.model.TransactionType
import java.util.UUID

@Entity(
    tableName = "transaction",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["account_id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["account_id"],
            childColumns = ["income_account_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo("transaction_id")
    val id: Long,
    val datetime: Long,
    val amount: Long,
    val type: TransactionType,
    val note: String,
    @ColumnInfo("account_id")
    val accountId: UUID,
    @ColumnInfo("category_id")
    val categoryId: Long?,
    @ColumnInfo("income_account_id")
    val incomeAccountId: UUID?,
)