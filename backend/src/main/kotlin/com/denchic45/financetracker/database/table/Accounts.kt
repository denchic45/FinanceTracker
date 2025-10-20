package com.denchic45.financetracker.database.table

import com.denchic45.financetracker.account.model.AccountType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.sum
import java.util.*


object Accounts : UUIDTable("account", "account_id") {
    val name = text("name")
    val type = enumerationByName<AccountType>("type", 16)
    val userId = reference("user_id", Users)
}

class AccountDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AccountDao>(Accounts)

    var name by Accounts.name
    var type by Accounts.type
    var user by UserDao referencedOn Accounts.userId

    val balance: Long
        get() = Transactions.select(Transactions.amount.sum()).groupBy(Transactions.amount.sum())
            .where { Transactions.accountId eq id }.first()[Transactions.amount.sum()] ?: 0
}