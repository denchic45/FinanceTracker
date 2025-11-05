package com.denchic45.financetracker.api.category.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCategoryRequest(
    val name: String,
    val icon: String
)