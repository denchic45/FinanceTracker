package com.denchic45.financetracker.database.table

import com.denchic45.financetracker.api.transaction.model.TransactionType
import com.denchic45.financetracker.database.table.Transactions.datetime
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.case
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.longLiteral
import org.jetbrains.exposed.v1.core.sum
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.datetime.datetime


object Transactions : LongIdTable("transaction", "transaction_id") {
    val datetime = datetime("datetime")
    val amount = long("amount")
    val type = enumerationByName<TransactionType>("transaction_type", 28)
    val description = text("description")
    val sourceAccountId = reference("source_account_id", Accounts, onDelete = ReferenceOption.CASCADE)
    val categoryId = optReference("category_id", Categories, onDelete = ReferenceOption.CASCADE)
    val incomeAccountId = optReference("income_account_id", Accounts, onDelete = ReferenceOption.CASCADE,)

    val expenseSum = case().When(type eq TransactionType.EXPENSE, amount).Else(longLiteral(0)).sum()

    val incomeSum = case().When(type eq TransactionType.INCOME, amount).Else(longLiteral(0)).sum()
}

class TransactionDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TransactionDao>(Transactions)

    var datetime by Transactions.datetime
    var amount by Transactions.amount
    var type by Transactions.type
    var description by Transactions.description
    var account by AccountDao referencedOn Transactions.sourceAccountId
    var category by CategoryDao optionalReferencedOn Transactions.categoryId
    var incomeAccount by AccountDao optionalReferencedOn Transactions.incomeAccountId
    var tags by TagDao via TransactionTags
}