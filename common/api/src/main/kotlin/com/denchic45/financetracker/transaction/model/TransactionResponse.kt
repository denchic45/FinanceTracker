package com.denchic45.financetracker.transaction.model

import com.denchic45.financetracker.account.model.AccountType
import com.denchic45.financetracker.category.model.CategoryResponse
import com.denchic45.financetracker.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

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
    val id: Long,
    val name: String,
    val accountType: AccountType,
    val balance: Long
)