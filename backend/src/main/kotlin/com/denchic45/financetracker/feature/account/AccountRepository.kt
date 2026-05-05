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
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import kotlin.uuid.Uuid

class AccountRepository() {

    fun add(request: CreateAccountRequest, ownerId: Uuid): Either<UserNotFound, AccountResponse> = either {
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

    fun findById(accountId: Uuid): Either<AccountNotFound, AccountResponse> = transaction {
        AccountDao.findById(accountId)?.toAccountResponse()?.right() ?: AccountNotFound.left()
    }

    fun update(accountId: Uuid, request: UpdateAccountRequest): Either<AccountNotFound, AccountResponse> = transaction {
        AccountDao.findById(accountId)?.apply {
            name = request.name
            type = request.type
            iconName = request.iconName
        }?.toAccountResponse()?.right() ?: AccountNotFound.left()
    }

    fun remove(accountId: Uuid): Either<AccountNotFound, Unit> = transaction {
        AccountDao.findById(accountId)?.delete()?.right() ?: AccountNotFound.left()
    }

    fun findAll(ownerId: Uuid): List<AccountResponse> = transaction {
        AccountDao.find(Accounts.ownerId eq ownerId).toAccountResponses()
    }
}