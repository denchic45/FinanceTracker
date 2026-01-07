package com.denchic45.financetracker.api.category.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryRequest(
    val name: String,
    val iconName: String,
    val income: Boolean
)