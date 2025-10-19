package com.denchic45.financetracker.data.mapper

import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.database.entity.TransactionEntity
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.transaction.model.TransactionResponse
import java.time.ZoneOffset

fun List<TransactionResponse>.toTransactionEntities() = map { response ->
    response.toTransactionEntity()
}

fun TransactionResponse.toTransactionEntity() = TransactionEntity(
    id = id,
    datetime = datetime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
    amount = amount,
    type = type,
    description = description,
    accountId = account.id,
    categoryId = category.id,
    incomeAccountId = incomeAccount?.id,
)

fun AggregatedTransactionEntity.toTransactionItem() = TransactionItem(
    id = id,
    amount = amount,
    type = type,
    description = description,
    account = with(account) {
        AccountItem(id, name, type, balance)
    },
    category = CategoryItem(categoryId, categoryName, categoryIcon, categoryIncome),
    incomeAccount = with(incomeAccount) {
        AccountItem(id, name, type, balance)
    }
)

fun List<AggregatedTransactionEntity>.toTransactionItems() =
    map(AggregatedTransactionEntity::toTransactionItem)