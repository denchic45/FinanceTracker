package com.denchic45.financetracker.feature.account

import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.database.table.AccountDao

fun AccountDao.toAccountResponse() = AccountResponse(
    id = id.value,
    name = name,
    type = type,
    initialBalance = initialBalance,
    balance = balance
)

fun Iterable<AccountDao>.toAccountResponses() = map(AccountDao::toAccountResponse)