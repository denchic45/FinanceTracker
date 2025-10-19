package com.denchic45.financetracker.feature.account

import com.denchic45.financetracker.account.model.AccountRequest
import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.database.table.AccountDao
import org.jetbrains.exposed.sql.transactions.transaction

class AccountRepository() {

    fun add(request: AccountRequest): AccountResponse = transaction {
        AccountDao.new {
            name = request.name
            type = request.type
        }.toAccountResponse()
    }

    fun findById(accountId: Long): AccountResponse = transaction {
        AccountDao[accountId].toAccountResponse()
    }

    fun update(accountId: Long, request: AccountRequest) = transaction {
        AccountDao[accountId].apply {
            name = request.name
            type = request.type
        }.toAccountResponse()
    }

    fun remove(accountId: Long) = transaction {
        AccountDao[accountId].delete()
    }

    fun findAll() = transaction {
        AccountDao.all().toAccountResponses()
    }
}