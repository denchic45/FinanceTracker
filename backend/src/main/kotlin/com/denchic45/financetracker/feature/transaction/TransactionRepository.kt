package com.denchic45.financetracker.feature.transaction

import com.denchic45.financetracker.database.table.AccountDao
import com.denchic45.financetracker.database.table.CategoryDao
import com.denchic45.financetracker.database.table.TransactionDao
import com.denchic45.financetracker.database.table.Transactions
import com.denchic45.financetracker.transaction.model.TransactionRequest
import com.denchic45.financetracker.transaction.model.TransactionResponse
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository() {

    fun add(request: TransactionRequest): TransactionResponse = transaction {
        TransactionDao.new {
            datetime = request.datetime
            amount = request.amount
            type = request.type
            description = request.description
            account = AccountDao[request.accountId]
            category = CategoryDao[request.categoryId]
            incomeAccount = request.incomeSourceId?.let { AccountDao[it] }
        }.toResponse()
    }

    fun find(page: Int, pageSize: Int) = transaction {
        TransactionDao.wrapRows(
            Transactions.selectAll()
                .limit(pageSize)
                .offset((page - 1).toLong() * pageSize)
        ).toTransactionResponses()
    }

    fun findById(transactionId: Long): TransactionResponse = transaction {
        TransactionDao[transactionId].toResponse()
    }

    fun update(transactionId: Long, request: TransactionRequest) = transaction {
        TransactionDao[transactionId].apply {
            datetime = request.datetime
            amount = request.amount
            type = request.type
            description = request.description
            account = AccountDao[request.accountId]
            category = CategoryDao[request.categoryId]
            incomeAccount = request.incomeSourceId?.let { AccountDao[it] }
        }.toResponse()
    }

    fun remove(transactionId: Long) = transaction {
        TransactionDao[transactionId].delete()
    }
}