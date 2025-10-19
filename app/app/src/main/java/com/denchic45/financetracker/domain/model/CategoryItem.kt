package com.denchic45.financetracker.domain.model


data class CategoryItem(
    val id: Long,
    val name: String,
    val icon: String,
    val income: Boolean
)