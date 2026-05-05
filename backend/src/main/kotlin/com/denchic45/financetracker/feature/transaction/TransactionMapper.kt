package com.denchic45.financetracker.feature.transaction

import com.denchic45.financetracker.api.transaction.model.*
import com.denchic45.financetracker.database.table.AccountDao
import com.denchic45.financetracker.database.table.TransactionDao
import com.denchic45.financetracker.feature.category.toCategoryResponse
import com.denchic45.financetracker.feature.tag.toTagResponses

fun AccountDao.toTransactionAccount() = TransactionAccount(
    id = id.value,
    name = name,
    accountType = type,
    initialBalance = initialBalance,
    balance = balance,
    iconName = iconName
)

fun Iterable<AccountDao>.toTransactionAccounts() = map(AccountDao::toTransactionAccount)

fun TransactionDao.toResponse() = toResponse(account.toTransactionAccount(), incomeAccount?.toTransactionAccount())

fun TransactionDao.toResponse(
    account: TransactionAccount,
    incomeAccount: TransactionAccount?
): AbstractTransactionResponse {
    return when (this.type) {
        TransactionType.EXPENSE, TransactionType.INCOME -> TransactionResponse(
            id = id.value,
            datetime = datetime,
            amount = amount,
            note = description,
            account = account,
            category = category!!.toCategoryResponse(),
            income = (type == TransactionType.INCOME),
            tags = tags.toTagResponses()
        )


        TransactionType.TRANSFER -> TransferTransactionResponse(
            id = id.value,
            datetime = datetime,
            amount = amount,
            note = description,
            account = account,
            incomeAccount = incomeAccount!!
        )
    }
}

fun Iterable<TransactionDao>.toTransactionResponses(accounts: List<TransactionAccount>) = map { transaction ->
    transaction.toResponse(
        accounts.first { it.id == transaction.account.id.value },
        transaction.incomeAccount?.let { incomeAccount -> accounts.first { it.id == incomeAccount.id.value } }
    )
}
