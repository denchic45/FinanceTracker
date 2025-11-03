package com.denchic45.financetracker.feature.transaction

import com.denchic45.financetracker.database.table.AccountDao
import com.denchic45.financetracker.database.table.TransactionDao
import com.denchic45.financetracker.feature.category.toCategoryResponse
import com.denchic45.financetracker.feature.tag.toTagResponses
import com.denchic45.financetracker.transaction.model.*

fun AccountDao.toTransactionAccount() = TransactionAccount(
    id = id.value,
    name = name,
    accountType = type,
    initialBalance = initialBalance,
    balance = balance
)

fun TransactionDao.toResponse(): AbstractTransactionResponse {
    return when (this.type) {
        TransactionType.EXPENSE, TransactionType.INCOME -> TransactionResponse(
            id = id.value,
            datetime = datetime,
            amount = amount,
            note = description,
            account = account.toTransactionAccount(),
            category = category!!.toCategoryResponse(),
            income = (type == TransactionType.INCOME),
            tags = tags.toTagResponses()
        )


        TransactionType.TRANSFER -> TransferTransactionResponse(
            id = id.value,
            datetime = datetime,
            amount = amount,
            note = description,
            account = account.toTransactionAccount(),
            incomeAccount = incomeAccount!!.toTransactionAccount()
        )

    }
}

fun Iterable<TransactionDao>.toTransactionResponses() = map(TransactionDao::toResponse)
