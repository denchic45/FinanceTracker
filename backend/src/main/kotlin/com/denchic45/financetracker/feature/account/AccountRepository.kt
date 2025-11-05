package com.denchic45.financetracker.feature.account

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.right
import com.denchic45.financetracker.api.account.model.AccountRequest
import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.database.table.AccountDao
import com.denchic45.financetracker.database.table.Accounts
import com.denchic45.financetracker.database.table.UserDao
import com.denchic45.financetracker.database.table.Users
import com.denchic45.financetracker.database.util.exists
import com.denchic45.financetracker.api.error.AccountNotFound
import com.denchic45.financetracker.api.error.UserNotFound
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AccountRepository() {

    fun add(request: AccountRequest, userId: UUID): Either<UserNotFound, AccountResponse> = either {
        transaction {
            AccountDao.new {
                name = request.name
                type = request.type
                initialBalance = request.initialBalance
                owner = ensureNotNull(UserDao.findById(userId)) { UserNotFound }
            }.toAccountResponse()
        }
    }

    fun findById(accountId: UUID): Either<AccountNotFound, AccountResponse> = transaction {
        AccountDao.findById(accountId)?.toAccountResponse()?.right() ?: AccountNotFound.left()
    }

    fun update(accountId: UUID, request: AccountRequest): Either<AccountNotFound, AccountResponse> = transaction {
        AccountDao.findById(accountId)?.apply {
            name = request.name
            type = request.type
        }?.toAccountResponse()?.right() ?: AccountNotFound.left()
    }

    fun remove(accountId: UUID): Either<AccountNotFound, Unit> = transaction {
        AccountDao.findById(accountId)?.delete()?.right() ?: AccountNotFound.left()
    }

    fun findAll(userId: UUID) = either {
        transaction {
            ensure(Users.exists { Users.id eq userId }) { UserNotFound }
            AccountDao.find(Accounts.ownerId eq userId).toAccountResponses()
        }
    }
}