package com.denchic45.financetracker.api.statistic.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupedAmountPoint(
    val periodLabel: String,
    val totalExpenses: Long,
    val totalIncomes: Long
)