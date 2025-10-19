package com.denchic45.financetracker.feature.transaction

import com.denchic45.financetracker.database.table.TransactionDao
import com.denchic45.financetracker.feature.category.toCategoryResponse
import com.denchic45.financetracker.transaction.model.TransactionAccount
import com.denchic45.financetracker.transaction.model.TransactionResponse

fun TransactionDao.toResponse() = TransactionResponse(
    id = id.value,
    datetime = datetime,
    amount = amount,
    type = type,
    description = description,
    account = TransactionAccount(
        id = account.id.value,
        name = account.name,
        accountType = account.type,
        balance = account.balance
    ),
    category = category.toCategoryResponse(),
    incomeAccount = incomeAccount?.let {
        TransactionAccount(
            it.id.value, it.name,
            it.type,
            it.balance
        )
    },
)

fun Iterable<TransactionDao>.toTransactionResponses() = map(TransactionDao::toResponse)