package com.denchic45.financetracker.api.statistic.model

import com.denchic45.financetracker.api.category.model.CategoryResponse
import kotlinx.serialization.Serializable

@Serializable
data class CategorizedAmounts(
    val expenses: List<CategoryAmount>,
    val incomes: List<CategoryAmount>
)

@Serializable
data class CategoryAmount(
    val item: CategoryResponse,
    val sum: Long,
    val count: Int
)