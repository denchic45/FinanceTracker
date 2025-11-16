package com.denchic45.financetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Junction table entity for the many-to-many relationship between transactions and tags.
 * This is equivalent to the server's TransactionTags table.
 */
@Entity(
    tableName = "transaction_tag",
    // Composite primary key of both foreign keys
    primaryKeys = ["transaction_id", "tag_id"], 
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["transaction_id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["tag_id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [
        Index(value = ["tag_id"]) 
    ]
)
data class TransactionTagCrossRef(
    @ColumnInfo(name = "transaction_id")
    val transactionId: Long,
    
    @ColumnInfo(name = "tag_id")
    val tagId: Long
)