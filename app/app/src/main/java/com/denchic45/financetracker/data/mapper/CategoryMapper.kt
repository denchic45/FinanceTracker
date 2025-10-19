package com.denchic45.financetracker.data.mapper

import com.denchic45.financetracker.category.model.CategoryResponse
import com.denchic45.financetracker.data.database.entity.CategoryEntity

fun List<CategoryResponse>.toCategoryEntities() = map { response ->
    response.toCategoryEntity()
}

fun CategoryResponse.toCategoryEntity() = CategoryEntity(
    id = id,
    name = name,
    icon = icon,
    income = income
)

fun CategoryEntity.toCategoryResponse() = CategoryResponse(
    id = id,
    name = name,
    icon = icon,
    income = income
)

fun List<CategoryEntity>.toCategoryResponses() = map(CategoryEntity::toCategoryResponse)

