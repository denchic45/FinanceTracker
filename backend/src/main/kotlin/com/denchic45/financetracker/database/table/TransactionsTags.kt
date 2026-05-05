package com.denchic45.financetracker.database.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass


object TransactionTags : LongIdTable("transaction_tag", "transaction_tag_id") {

    val transactionId = reference("transaction_id", Transactions, onDelete = ReferenceOption.CASCADE)

    val tagId = reference("tag_id", Tags, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex(transactionId, tagId)
    }
}

class TransactionTagDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TransactionTagDao>(TransactionTags)

    var transaction by TransactionDao referencedOn TransactionTags.transactionId
    var tag by TagDao referencedOn TransactionTags.tagId
}