package com.denchic45.financetracker.feature.category

import com.denchic45.financetracker.database.table.CategoryDao
import com.denchic45.financetracker.category.model.CategoryRequest
import com.denchic45.financetracker.category.model.CategoryResponse
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryRepository() {

    fun add(request: CategoryRequest): CategoryResponse = transaction {
        CategoryDao.new {
            name = request.name
            icon = request.icon
            income = request.income
        }.toCategoryResponse()
    }

    fun findById(categoryId: Long): CategoryResponse = transaction {
        CategoryDao[categoryId].toCategoryResponse()
    }

    fun update(categoryId: Long, request: CategoryRequest) = transaction {
        CategoryDao[categoryId].apply {
            name = request.name
            icon = request.icon
            income = request.income
        }.toCategoryResponse()
    }

    fun remove(categoryId: Long) = transaction {
        CategoryDao[categoryId].delete()
    }
}