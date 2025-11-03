package com.denchic45.financetracker.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption


object TransactionTags : LongIdTable("transaction_tag", "transaction_tag_id") {

    val transactionId = reference("transaction_id", Transactions, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)

    val tagId = reference("tag_id", Tags, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)

    init {
        uniqueIndex(transactionId, tagId)
    }
}

class TransactionTagDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TransactionTagDao>(TransactionTags)

    var transaction by TransactionDao referencedOn TransactionTags.transactionId
    var tag by TagDao referencedOn TransactionTags.tagId
}