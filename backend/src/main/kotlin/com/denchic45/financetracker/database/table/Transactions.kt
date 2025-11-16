package com.denchic45.financetracker.database.table

import com.denchic45.financetracker.api.transaction.model.TransactionType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.case
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.sum

object Transactions : LongIdTable("transaction", "transaction_id") {
    val datetime = datetime("datetime")
    val amount = long("amount")
    val type = enumerationByName<TransactionType>("transaction_type", 28)
    val description = text("description")
    val sourceAccountId = reference(
        "source_account_id", Accounts,
        onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE
    )
    val categoryId = optReference(
        "category_id",
        Categories,
        onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE
    )
    val incomeAccountId = optReference(
        "income_account_id",
        Accounts,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

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