package com.denchic45.financetracker.statistic.model

import com.denchic45.financetracker.account.model.AccountResponse
import kotlinx.serialization.Serializable

@Serializable
data class AccountStatisticsResponse(
    val account: AccountResponse,
    val expenses: Long,
    val incomes: Long,
    val profit: Long
)