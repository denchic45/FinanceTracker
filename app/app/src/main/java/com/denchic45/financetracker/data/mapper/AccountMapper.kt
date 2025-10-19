package com.denchic45.financetracker.data.mapper

import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.data.database.entity.AccountEntity
import com.denchic45.financetracker.transaction.model.TransactionAccount

fun List<AccountResponse>.toAccountEntities() = map { response ->
    response.toAccountEntity()
}

fun AccountResponse.toAccountEntity() = AccountEntity(
    id = id,
    name = name,
    type = type,
    balance = balance
)

fun TransactionAccount.toAccountEntity() = AccountEntity(
    id = id,
    name = name,
    type = accountType,
    balance = balance
)

fun AccountEntity.toAccountResponse() = AccountResponse(
    id = id,
    name = name,
    type = type,
    balance = balance
)

fun List<AccountEntity>.toAccountResponses() = map(AccountEntity::toAccountResponse)

