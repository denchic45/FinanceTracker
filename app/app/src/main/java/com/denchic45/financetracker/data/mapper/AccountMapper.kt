package com.denchic45.financetracker.data.mapper

import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.data.database.entity.AccountEntity
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.api.transaction.model.TransactionAccount

fun List<AccountResponse>.toAccountEntities() = map { response ->
    response.toAccountEntity()
}

fun AccountResponse.toAccountEntity() = AccountEntity(
    id = id,
    name = name,
    type = type,
    initialBalance = initialBalance,
    balance = balance
)

fun TransactionAccount.toAccountEntity() = AccountEntity(
    id = id,
    name = name,
    type = accountType,
    initialBalance = initialBalance,
    balance = balance
)

fun AccountEntity.toAccountItem() = AccountItem(
    id = id,
    name = name,
    type = type,
    balance = balance
)

fun List<AccountEntity>.toAccountItems() = map(AccountEntity::toAccountItem)

