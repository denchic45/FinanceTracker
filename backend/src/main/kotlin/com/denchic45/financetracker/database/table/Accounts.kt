package com.denchic45.financetracker.database.table

import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.api.transaction.model.TransactionType
import com.denchic45.financetracker.database.table.Transactions.amount
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import org.jetbrains.exposed.v1.jdbc.select
import kotlin.uuid.Uuid


object Accounts : UuidTable("account", "account_id") {
    val name = text("name")
    val type = enumerationByName<AccountType>("type", 16)
    val initialBalance = long("initial_balance")
    val iconName = text("icon_name")
    val ownerId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
}

class AccountDao(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<AccountDao>(Accounts) {
        fun getByIds(accountIds: Set<Uuid>) = find { Accounts.id inList accountIds }

    }

    var name by Accounts.name
    var type by Accounts.type
    var initialBalance by Accounts.initialBalance
    var iconName by Accounts.iconName
    var owner by UserDao referencedOn Accounts.ownerId


    //    val balance: Long = 0
    val balance: Long
        get() {
            val transferExpenseSum = transferExpenseSum(listOf(id.value))
            val transferIncomeSum = transferIncomeSum(listOf(id.value))

            return (Transactions.select(
                Transactions.expenseSum,
                Transactions.incomeSum,
                transferExpenseSum,
                transferIncomeSum
            )
                .where(Transactions.sourceAccountId eq id or (Transactions.incomeAccountId eq id))
                .firstOrNull()?.let { row ->
                    val expenses = row[Transactions.expenseSum] ?: 0L
                    val incomes = row[Transactions.incomeSum] ?: 0L
                    val transferExpenses = (row[transferExpenseSum] ?: 0)
                    val transferIncomes = (row[transferIncomeSum] ?: 0)
                    val allExpenses = expenses + transferExpenses
                    val allIncomes = incomes + transferIncomes
                    allIncomes - allExpenses
                } ?: 0) + initialBalance
        }
}

fun transferExpenseSum(accountIds: List<Uuid>) = case()
    .When(Transactions.type eq TransactionType.TRANSFER and (Transactions.sourceAccountId inList accountIds), amount)
    .Else(longLiteral(0))
    .sum()

fun transferIncomeSum(accountIds: List<Uuid>) = case()
    .When(Transactions.type eq TransactionType.TRANSFER and (Transactions.incomeAccountId inList accountIds), amount)
    .Else(longLiteral(0))
    .sum()