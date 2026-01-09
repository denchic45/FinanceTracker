package com.denchic45.financetracker.feature.category

import com.denchic45.financetracker.database.table.CategoryDao
import com.denchic45.financetracker.api.category.model.CategoryResponse

fun CategoryDao.toCategoryResponse() = CategoryResponse(
    id=id.value,
    name = name,
    iconName = iconName,
    income = income
)

fun Iterable<CategoryDao>.toCategoryResponses() = map(CategoryDao::toCategoryResponse)