package com.denchic45.financetracker.data.mapper

import com.denchic45.financetracker.api.transaction.model.AbstractTransactionResponse
import com.denchic45.financetracker.api.transaction.model.TransactionResponse
import com.denchic45.financetracker.api.transaction.model.TransactionType
import com.denchic45.financetracker.api.transaction.model.TransferTransactionResponse
import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.database.entity.TransactionEntity
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.TransactionItem
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


private fun AbstractTransactionResponse.mapToTransactionType(): TransactionType {
    return when (this) {
        is TransactionResponse -> if (income) TransactionType.INCOME else TransactionType.EXPENSE
        is TransferTransactionResponse -> TransactionType.TRANSFER
    }
}

@OptIn(ExperimentalTime::class)
fun AbstractTransactionResponse.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        datetime = datetime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        amount = amount,
        note = note,
        accountId = account.id,
        type = mapToTransactionType(),
        categoryId = (this as? TransactionResponse)?.category?.id,
        incomeAccountId = (this as? TransferTransactionResponse)?.incomeAccount?.id
    )
}

@OptIn(ExperimentalTime::class)
fun AggregatedTransactionEntity.toTransactionItem(): TransactionItem {
    val accountItem = with(account) {
        AccountItem(id, name, type, balance)
    }

    return when (type) {
        TransactionType.EXPENSE -> TransactionItem.Expense(
            id = id,
            amount = amount,
            datetime = Instant.fromEpochMilliseconds(datetime)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
            note = note,
            account = accountItem,
            category = category!!.toCategoryItem(),
            tags = tags.toTagItems()
        )

        TransactionType.INCOME -> TransactionItem.Income(
            id = id,
            amount = amount,
            datetime = Instant.fromEpochMilliseconds(datetime)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
            note = note,
            account = accountItem,
            category = category!!.toCategoryItem(),
            tags = tags.toTagItems()
        )

        TransactionType.TRANSFER -> TransactionItem.Transfer(
            id = id,
            amount = amount,
            datetime = Instant.fromEpochMilliseconds(datetime)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
            note = note,
            account = accountItem,
            incomeAccount = with(incomeAccount!!) {
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