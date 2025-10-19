package com.denchic45.financetracker.database.table

import com.denchic45.financetracker.transaction.model.TransactionType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Transactions : LongIdTable("transaction", "transaction_id") {
    val date = datetime("date")
    val amount = long("amount")
    val type = enumerationByName<TransactionType>("transaction_type", 28)
    val description = text("description")
    val accountId = reference("source_id", Accounts)
    val categoryId = reference("category_id", Categories)
    val incomeAccountId = optReference("income_account", Accounts)
}

class TransactionDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TransactionDao>(Transactions)

    var datetime by Transactions.date
    var amount by Transactions.amount
    var type by Transactions.type
    var description by Transactions.description
    var account by AccountDao referencedOn Transactions.accountId
    var category by CategoryDao referencedOn Transactions.categoryId
    var incomeAccount by AccountDao optionalReferencedOn Transactions.incomeAccountId
}