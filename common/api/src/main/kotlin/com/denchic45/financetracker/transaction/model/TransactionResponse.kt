package com.denchic45.financetracker.transaction.model

import com.denchic45.financetracker.account.model.AccountType
import com.denchic45.financetracker.category.model.CategoryResponse
import com.denchic45.financetracker.util.LocalDateTimeSerializer
import com.denchic45.financetracker.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class TransactionResponse(
    val id: Long,
    @Serializable(LocalDateTimeSerializer::class)
    val datetime: LocalDateTime,
    val amount: Long,
    val type: TransactionType,
    val description: String,
    val account: TransactionAccount,
    val category: CategoryResponse,
    val incomeAccount: TransactionAccount? = null
)

@Serializable
data class TransactionAccount(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val accountType: AccountType,
    val balance: Long
)