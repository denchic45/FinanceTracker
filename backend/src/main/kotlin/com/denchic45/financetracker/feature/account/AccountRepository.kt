package com.denchic45.financetracker.feature.account

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.right
import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.api.account.model.CreateAccountRequest
import com.denchic45.financetracker.api.account.model.UpdateAccountRequest
import com.denchic45.financetracker.api.error.AccountNotFound
import com.denchic45.financetracker.api.error.UserNotFound
import com.denchic45.financetracker.database.table.AccountDao
import com.denchic45.financetracker.database.table.Accounts
import com.denchic45.financetracker.database.table.UserDao
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AccountRepository() {

    fun add(request: CreateAccountRequest, ownerId: UUID): Either<UserNotFound, AccountResponse> = either {
        transaction {
            AccountDao.new {
                name = request.name
                type = request.type
                initialBalance = request.initialBalance
                iconName = request.iconName
                owner = ensureNotNull(UserDao.findById(ownerId)) { UserNotFound }
            }.toAccountResponse()
        }
    }

    fun findById(accountId: UUID): Either<AccountNotFound, AccountResponse> = transaction {
        AccountDao.findById(accountId)?.toAccountResponse()?.right() ?: AccountNotFound.left()
    }

    fun update(accountId: UUID, request: UpdateAccountRequest): Either<AccountNotFound, AccountResponse> = transaction {
        AccountDao.findById(accountId)?.apply {
            name = request.name
            type = request.type
            iconName = request.iconName
        }?.toAccountResponse()?.right() ?: AccountNotFound.left()
    }

    fun remove(accountId: UUID): Either<AccountNotFound, Unit> = transaction {
        AccountDao.findById(accountId)?.delete()?.right() ?: AccountNotFound.left()
    }

    fun findAll(ownerId: UUID): List<AccountResponse> = transaction {
        AccountDao.find(Accounts.ownerId eq ownerId).toAccountResponses()
    }
}