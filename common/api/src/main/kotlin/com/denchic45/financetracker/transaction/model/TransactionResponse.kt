package com.denchic45.financetracker.transaction.model

import com.denchic45.financetracker.account.model.AccountType
import com.denchic45.financetracker.category.model.CategoryResponse
import com.denchic45.financetracker.util.LocalDateTimeSerializer
import com.denchic45.financetracker.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
sealed class AbstractTransactionResponse() {
    abstract val id: Long
    abstract val datetime: LocalDateTime
    abstract val amount: Long
    abstract val description: String
    abstract val account: TransactionAccount
}

@Serializable
data class TransactionResponse(
    override val id: Long,
    @Serializable(LocalDateTimeSerializer::class)
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val description: String,
    override val account: TransactionAccount,
    val category: CategoryResponse,
    val income: Boolean
) : AbstractTransactionResponse()

@Serializable
data class TransferTransactionResponse(
    override val id: Long,
    @Serializable(LocalDateTimeSerializer::class)
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val description: String,
    override val account: TransactionAccount,
    val incomeAccount: TransactionAccount,
) : AbstractTransactionResponse()

@Serializable
data class TransactionAccount(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val accountType: AccountType,
    val balance: Long
)