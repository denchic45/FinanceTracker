package com.denchic45.financetracker.feature.transaction

import com.denchic45.financetracker.database.table.Transactions
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.SortOrder
import kotlin.uuid.Uuid

@Serializable
data class TransactionFilters(
    val page: Int,
    val pageSize: Int,
    val accountIds: List<Uuid>?,
    val categoryIds: List<Long>?,
    val tagIds: List<Long>?,
    val fromDate: LocalDate?,
    val toDate: LocalDate?,

    val sortBy: TransactionSortField = TransactionSortField.DATE,
    val sortOrder: SortOrder = SortOrder.DESC
)

enum class TransactionSortField(val column: Column<*>) {
    DATE(Transactions.datetime),
    AMOUNT(Transactions.amount);

    companion object {
        fun fromString(value: String?) = entries.find {
            it.name.equals(value, ignoreCase = true)
        } ?: DATE
    }
}