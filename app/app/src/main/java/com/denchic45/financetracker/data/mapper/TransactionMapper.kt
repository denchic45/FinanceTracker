package com.denchic45.financetracker.data.mapper

import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.database.entity.TransactionEntity
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.transaction.model.AbstractTransactionResponse
import com.denchic45.financetracker.transaction.model.TransactionResponse
import com.denchic45.financetracker.transaction.model.TransactionType
import com.denchic45.financetracker.transaction.model.TransferTransactionResponse
import java.time.ZoneOffset


private fun AbstractTransactionResponse.mapToTransactionType(): TransactionType {
    return when (this) {
        is TransactionResponse -> if (income) TransactionType.INCOME else TransactionType.EXPENSE
        is TransferTransactionResponse -> TransactionType.TRANSFER
    }
}

fun AbstractTransactionResponse.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        datetime = datetime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
        amount = amount,
        description = description,
        accountId = account.id,
        type = mapToTransactionType(),
        categoryId = (this as? TransactionResponse)?.category?.id,
        incomeAccountId = (this as? TransferTransactionResponse)?.incomeAccount?.id
    )
}

fun AggregatedTransactionEntity.toTransactionItem(): TransactionItem {
    val accountItem = with(account) {
        AccountItem(id, name, type, balance)
    }
    val categoryItem = CategoryItem(
        id = categoryId,
        name = categoryName,
        icon = categoryIcon,
        income = categoryIncome
    )
    return when (type) {
        TransactionType.EXPENSE -> TransactionItem.Expense(
            id = id,
            amount = amount,
            note = description,
            account = accountItem,
            category = categoryItem
        )

        TransactionType.INCOME -> TransactionItem.Income(
            id = id,
            amount = amount,
            note = description,
            account = accountItem,
            category = categoryItem
        )

        TransactionType.TRANSFER -> TransactionItem.Transfer(
            id = id,
            amount = amount,
            note = description,
            account = accountItem,
            incomeAccount = with(incomeAccount) {
                AccountItem(id, name, type, balance)
            }
        )
    }
}

fun List<AggregatedTransactionEntity>.toTransactionItems() =
    map(AggregatedTransactionEntity::toTransactionItem)

fun List<AbstractTransactionResponse>.toTransactionEntities() = map { response ->
    response.toTransactionEntity()
}