package com.denchic45.financetracker.database.table

import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.database.table.Transactions.amount
import com.denchic45.financetracker.api.transaction.model.TransactionType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.case
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.sum
import java.util.*


object Accounts : UUIDTable("account", "account_id") {
    val name = text("name")
    val type = enumerationByName<AccountType>("type", 16)
    val initialBalance = long("initial_balance")
    val ownerId = reference("user_id", Users,
        onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)

}

class AccountDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AccountDao>(Accounts)

    var name by Accounts.name
    var type by Accounts.type
    var initialBalance by Accounts.initialBalance
    var owner by UserDao referencedOn Accounts.ownerId


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

fun transferExpenseSum(accountIds: List<UUID>) = case()
    .When(Transactions.type eq TransactionType.TRANSFER and (Transactions.sourceAccountId inList accountIds), amount)
    .Else(longLiteral(0))
    .sum()

fun transferIncomeSum(accountIds: List<UUID>) = case()
    .When(Transactions.type eq TransactionType.TRANSFER and (Transactions.incomeAccountId inList accountIds), amount)
    .Else(longLiteral(0))
    .sum()